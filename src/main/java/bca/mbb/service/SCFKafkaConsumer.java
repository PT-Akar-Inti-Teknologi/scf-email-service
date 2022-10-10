package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.UploadInvoiceClient;
import bca.mbb.config.MultipartInputStreamFileResource;
import bca.mbb.dto.InvoiceError;
import bca.mbb.dto.TransactionDetailDto;
import bca.mbb.dto.TransactionHeaderDto;
import bca.mbb.dto.foundation.FoundationKafkaBulkUpdateDto;
import bca.mbb.entity.FoInvoiceErrorDetailEntity;
import bca.mbb.entity.FoTransactionDetailEntity;
import bca.mbb.entity.FoTransactionHeaderEntity;
import bca.mbb.enums.ActionEnum;
import bca.mbb.enums.StatusEnum;
import bca.mbb.repository.FoInvoiceErrorDetailRepository;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.scf.avro.AuthorizeUploadData;
import bca.mbb.scf.avro.NotificationData;
import bca.mbb.scf.avro.TransactionData;
import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class SCFKafkaConsumer {

    @Value("${channel-id}")
    private String channelId;
    @Value("${ws-id}")
    private String wsId;
    @Value("${success-code}")
    private String successCode;
    @Value("${cutoff-code}")
    private String errorCutoffCode;
    private final UploadInvoiceClient uploadInvoiceClient;
    @Value("${app.kafka.topic.others-to-foundation-bulk}")
    private String othersToFoundation;
    @Value("${app.kafka.topic.core-transaction}")
    private String transactionDataTopic;

    private final FoInvoiceErrorDetailRepository foInvoiceErrorDetailRepository;
    private final FoTransactionHeaderRepository foTransactionHeaderRepository;
    private final FoTransactionDetailRepository foTransactionDetailRepository;
    private final MessagingService<SpecificRecordBase> messagingService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final String CORE_CHANNEL = "CORE-SCF";
    private static final String USER_SYSTEM = "SYSTEM";
    private static final String LOAN_UPLOAD_INVOICE = "LOAN_UPLOAD_INVOICE";

    @KafkaListener(topics = "#{'${app.kafka.topic.notification}_${channel-id}'}", groupId = "#{'${spring.kafka.consumer.group-id-notification}'}", containerFactory = "validateDoneListener")
    public void validateDoneListen(NotificationData message) throws JsonProcessingException {
        if(message.getChannelId().equalsIgnoreCase(channelId)) {

            var header = foTransactionHeaderRepository.findByChainingIdAndTransactionName(message.getChainingId(), ActionEnum.UPLOAD_INVOICE.name());

            if (message.getStatus().equalsIgnoreCase(StatusEnum.SUCCESS.name())) {
                header.setStatus(StatusEnum.SUCCESS);
            } else {
                header.setStatus(StatusEnum.FAILED);
                header.setReason(null);

                var listError = mapper.readValue(message.getListInvoiceErrorJson(), new TypeReference<ArrayList<InvoiceError>>() {
                });

                listError.forEach(error -> {
                    var boInvoiceErrorDetail = new FoInvoiceErrorDetailEntity();
                    boInvoiceErrorDetail.setFoInvoiceErrorDetailId(CommonUtil.uuid());
                    boInvoiceErrorDetail.setChainingId(message.getChainingId());
                    boInvoiceErrorDetail.setErrorCode(error.getErrorCode());
                    boInvoiceErrorDetail.setErrorDescriptionEng(error.getErrorDescEng());
                    boInvoiceErrorDetail.setErrorDescriptionInd(error.getErrorDescInd());
                    boInvoiceErrorDetail.setLine(error.getLine());

                    foInvoiceErrorDetailRepository.save(boInvoiceErrorDetail);
                });
            }

            foTransactionHeaderRepository.save(header);

            othersToFoundationKafkaUpdate(header);
        }
    }

    @KafkaListener(topics = "#{'${app.kafka.topic.authorize-upload}_${channel-id}'}", groupId = "#{'${spring.kafka.consumer.group-id-notification}'}", containerFactory = "uploadValidatedInvoiceListener")
    public void uploadValidatedInvoiceListen(AuthorizeUploadData message) throws IOException {
        var foTransactionHeader = foTransactionHeaderRepository.findByFoTransactionHeaderId(message.getFoTransactionHeaderId());
        var foTransactionDetail = foTransactionDetailRepository.findAllByFoTransactionHeaderIdOrderByLineNumberAsc(message.getFoTransactionHeaderId());
        var filename = foTransactionHeader.getFileName();
        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var file = File.createTempFile(filename, ".txt");
        var printStream = new PrintStream(file);
        var transactionType = foTransactionHeader.getTransactionType().equalsIgnoreCase(ActionEnum.ADD.name()) ? "Tambah" : "Hapus";

        printStream.print("0|" + transactionType + "|||" + foTransactionHeader.getCorporateCode() + "|" + (foTransactionHeader.getFileHeaderId() != null ? foTransactionHeader.getFileHeaderId() : "") + "|||||||||||||||||" + "\n");

        foTransactionDetail.forEach(detail ->
                printStream.print(
                        "1" + "|" +
                                detail.getInvoiceNumber() + "|" +
                                detail.getInvoiceDate().format(formatter) + "|" +
                                detail.getInvoiceDueDate().format(formatter) + "|" +
                                detail.getCurrency() + "|" +
                                detail.getInvoiceAmount() + "|" +
                                detail.getSellerCode() + "|" +
                                detail.getBuyerCode() + "|" +
                                detail.getProgramCode() + "|" +
                                detail.getRemarks() + "\n"
                )
        );

        printStream.flush();
        printStream.close();

        var body  = new LinkedMultiValueMap<String, Object>();
        body.add("file", new MultipartInputStreamFileResource(new FileInputStream(file), filename));
        body.add("ws-id", wsId);
        body.add("party-type", foTransactionHeader.getPrimaryPartyType());
        body.add("party-code", foTransactionHeader.getPrimaryPartyCode());
        body.add("party-name", foTransactionHeader.getPrimaryPartyName());
        body.add("corporate-code", foTransactionHeader.getCorporateCode());
        body.add("chaining-id", foTransactionHeader.getChainingId());
        body.add("transaction-type", foTransactionHeader.getTransactionType());
        body.add("remarks", foTransactionHeader.getRemarks());

        var response = uploadInvoiceClient.uploadValidatedInvoice(message.getUser(), channelId, body).getBody();

        if(!response.getErrorCode().equalsIgnoreCase(successCode)){
            foTransactionHeader.setStatus(StatusEnum.FAILED);
            foTransactionHeader.setReason(response.getErrorMessageEn());

            if(response.getErrorCode().equalsIgnoreCase(errorCutoffCode)){
                var totalRecords = foTransactionDetail.size() + 1;

                var foDetailError = new FoInvoiceErrorDetailEntity();
                foDetailError.setFoInvoiceErrorDetailId(CommonUtil.uuid());
                foDetailError.setErrorCode(response.getErrorCode());
                foDetailError.setErrorDescriptionEng(response.getErrorMessageEn());
                foDetailError.setErrorDescriptionInd(response.getErrorMessageInd());
                foDetailError.setChainingId(foTransactionHeader.getChainingId());
                foDetailError.setLine(IntStream.range(1, totalRecords).boxed().map(String::valueOf).collect(Collectors.joining(",")));
                foInvoiceErrorDetailRepository.save(foDetailError);
            }
        }

        othersToFoundationKafkaUpdate(foTransactionHeader);
    }

    @KafkaListener(topics = "#{'${app.kafka.topic.channel-transaction}_${channel-id}'}", groupId = "#{'${spring.kafka.consumer.group-id-transaction}'}", containerFactory = "channelSynchronizerListener")
    public void channelSynchronizerListen(TransactionData message) throws JsonProcessingException {

        if (message.getChannelId().equalsIgnoreCase(channelId)  && message.getEntityName() != null && message.getEntityValue() != null) {

            var entityName = message.getEntityName();
            var entityValue = message.getEntityValue();

            var prefix = "";

            if(message.getTransactionType().equalsIgnoreCase(ActionEnum.REQUEST_FINANCE.name())){
                prefix = "REQ";
            }

            if(message.getTransactionType().equalsIgnoreCase(ActionEnum.RESERVE_LIMIT.name())){
                prefix = "RL";
            }

            if (entityName.equalsIgnoreCase("TransactionHeader")) {
                var transactionHeader = mapper.readValue(entityValue, TransactionHeaderDto.class);
                var foTransactionHeader = foTransactionHeaderRepository.findByTransactionHeaderId(transactionHeader.getTransactionHeaderId());

                createTransactionHeader(transactionHeader, foTransactionHeader, prefix, message);
            }

            if (entityName.equalsIgnoreCase("TransactionDetail")) {
                var transactionDetail = mapper.readValue(entityValue, TransactionDetailDto.class);
                var foTransactionDetail = foTransactionDetailRepository.findByTransactionDetailId(transactionDetail.getTransactionDetailId());

                createTransactionDetail(transactionDetail, foTransactionDetail, prefix, message);
            }
        }
    }

    private void createTransactionHeader(TransactionHeaderDto transactionHeader, FoTransactionHeaderEntity foTransactionHeader, String prefix, TransactionData message) throws JsonProcessingException {
        if(foTransactionHeader == null && transactionHeader.getChannelReferenceNumber() != null){
            foTransactionHeader = foTransactionHeaderRepository.findByReferenceNumber(transactionHeader.getChannelReferenceNumber());
        }

        if(foTransactionHeader == null) {
            foTransactionHeader = new FoTransactionHeaderEntity();
            foTransactionHeader.setReferenceNumber(foTransactionHeaderRepository.getChannelRefnoSequence(prefix));
            transactionHeader.setChannelReferenceNumber(foTransactionHeader.getReferenceNumber());

            foTransactionHeader.setRequestedBy(USER_SYSTEM);
            foTransactionHeader.setAuthorizedBy(USER_SYSTEM);
            foTransactionHeader.setRequestedDate(transactionHeader.getExecutedDate());
            foTransactionHeader.setAuthorizedDate(transactionHeader.getExecutedDate());

            BeanUtils.copyProperties(transactionHeader, foTransactionHeader);
            foTransactionHeader.setReason(transactionHeader.getFailedReason());

            foTransactionHeaderRepository.save(foTransactionHeader);

            sendTransactionData(message, message.getEntityName(), mapper.writeValueAsString(transactionHeader));
        }
        else {
            foTransactionHeader.setReason(transactionHeader.getFailedReason());
            BeanUtils.copyProperties(transactionHeader, foTransactionHeader, CommonUtil.getNullPropertyNames(transactionHeader));
            foTransactionHeaderRepository.save(foTransactionHeader);
        }
    }

    private void createTransactionDetail(TransactionDetailDto transactionDetail, FoTransactionDetailEntity foTransactionDetail, String prefix, TransactionData message) throws JsonProcessingException {
        if(foTransactionDetail == null && transactionDetail.getChannelReferenceNumber() != null){
            foTransactionDetail = foTransactionDetailRepository.findByReferenceNumber(transactionDetail.getChannelReferenceNumber());
        }

        if(foTransactionDetail == null) {
            var foTransactionHeader = foTransactionHeaderRepository.findByTransactionHeaderId(transactionDetail.getTransactionHeaderId());
            var referenceNUmber = foTransactionHeader.getReferenceNumber();

            if(foTransactionHeader.getTotalRecord() > 1){
                referenceNUmber = foTransactionHeaderRepository.getChannelRefnoSequence(prefix);
            }

            foTransactionDetail = new FoTransactionDetailEntity();
            foTransactionDetail.setFoTransactionHeaderId(foTransactionHeader.getFoTransactionHeaderId());
            foTransactionDetail.setReferenceNumber(referenceNUmber);
            transactionDetail.setChannelReferenceNumber(foTransactionDetail.getReferenceNumber());

            if(foTransactionHeader.getFinanceTenor() == null || !foTransactionHeader.getFinanceTenor().equals(transactionDetail.getTenorValue())) {
                foTransactionHeader.setFinanceTenor(transactionDetail.getTenorValue());
            }

            BeanUtils.copyProperties(transactionDetail, foTransactionDetail);
            foTransactionDetail.setReason(transactionDetail.getFailedReason());
            foTransactionDetailRepository.save(foTransactionDetail);

            sendTransactionData(message, message.getEntityName(), mapper.writeValueAsString(transactionDetail));
        }
        else {
            foTransactionDetail.setReason(transactionDetail.getFailedReason());

            if(transactionDetail.getChannelReferenceNumber().contains("RFN")){
                foTransactionDetail.setRepaymentAmount(transactionDetail.getTransactionAmount());
            }
            else if(transactionDetail.getChannelReferenceNumber().contains("PIN") && transactionDetail.getProductCode().equalsIgnoreCase("refact")){
                foTransactionDetail.setPaymentAmount(transactionDetail.getTransactionAmount());
            }

            BeanUtils.copyProperties(transactionDetail, foTransactionDetail, CommonUtil.getNullPropertyNames(transactionDetail));
            foTransactionDetailRepository.save(foTransactionDetail);
        }
    }

    private void sendTransactionData(TransactionData message, String entityName, String entityAsString){

        if(message.getTransactionType().equalsIgnoreCase(ActionEnum.REQUEST_FINANCE.name()) || message.getTransactionType().equalsIgnoreCase(ActionEnum.RESERVE_LIMIT.name())) {

            message = TransactionData.newBuilder()
                    .setChannelId(CORE_CHANNEL)
                    .setEntityName(entityName)
                    .setEntityValue(entityAsString).build();

            messagingService.sendMessage(transactionDataTopic, message);
        }

    }

    private void othersToFoundationKafkaUpdate(FoTransactionHeaderEntity foTransactionHeader) {
        try {
            var currency = foTransactionDetailRepository.getCurrencyByFoTransactionId(foTransactionHeader.getFoTransactionHeaderId());

            messagingService.sendMessage(othersToFoundation, TransactionBulk.newBuilder()
                    .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaBulkUpdateDto.builder().corpId(foTransactionHeader.getCorporateCode())
//                                    .userId()
                            .transactionType(LOAN_UPLOAD_INVOICE)
                            .streamTransactionId(foTransactionHeader.getChainingId())
                            .transactionAmount(foTransactionHeader.getTotalAmount())
                            .transactionCurrency(currency == null ? null : currency)
                            .transactionStatus(foTransactionHeader.getStatus().name())
                            .transactionDetails((foTransactionHeader.getTransactionType().equalsIgnoreCase(ActionEnum.ADD.name()) ? "Tambah" : "Hapus") +" – " + foTransactionHeader.getRemarks())
                            .transactionEffectiveDate(foTransactionHeader.getEffectiveDate())
                            .rejectCancelReason(foTransactionHeader.getReason())))
                    .build());
        } catch (Exception e) {
            foTransactionHeader.setWorkflowFailure(StatusEnum.UPDATE);
            foTransactionHeader.setUpdatedDate(LocalDateTime.now());
            foTransactionHeaderRepository.save(foTransactionHeader);
        }
    }
}