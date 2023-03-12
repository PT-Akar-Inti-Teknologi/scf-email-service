package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.UploadInvoiceClient;
import bca.mbb.config.MultipartInputStreamFileResource;
import bca.mbb.dto.Constant;
import bca.mbb.dto.InvoiceError;
import bca.mbb.dto.foundation.UserDetailsDto;
import bca.mbb.repository.FoInvoiceErrorDetailRepository;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.scf.avro.AuthorizeUploadData;
import bca.mbb.scf.avro.NotificationData;
import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lib.fo.entity.FoInvoiceErrorDetailEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final FoInvoiceErrorDetailRepository foInvoiceErrorDetailRepository;
    private final FoTransactionHeaderRepository foTransactionHeaderRepository;
    private final FoTransactionDetailRepository foTransactionDetailRepository;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final FoundationService foundationService;

    @KafkaListener(topics = "#{'${app.kafka.topic.notification}_${channel-id}'}", groupId = "#{'${spring.kafka.consumer.group-id-notification}'}", containerFactory = "validateDoneListener")
    public void validateDoneListen(NotificationData message) throws JsonProcessingException {
        if(message.getChannelId().equalsIgnoreCase(channelId)) {

            var header = foTransactionHeaderRepository.findByChainingIdAndTransactionName(message.getChainingId(), ActionEnum.UPLOAD_INVOICE.name());

            if (message.getStatus().equalsIgnoreCase(StatusEnum.SUCCESS.name())) {
                header.setStatus(StatusEnum.DONE);
            } else {
                header.setStatus(StatusEnum.FAILED);
                header.setReason(null);

                var listError = mapper.readValue(message.getListInvoiceErrorJson(), new TypeReference<ArrayList<InvoiceError>>() {
                });

                listError.forEach(error ->
                    foInvoiceErrorDetailRepository.save(FoInvoiceErrorDetailEntity.builder()
                            .foInvoiceErrorDetailId(CommonUtil.uuid())
                            .chainingId(message.getChainingId())
                            .errorCode(error.getErrorCode())
                            .errorDescriptionEng(error.getErrorDescEng())
                            .errorDescriptionInd(error.getErrorDescInd())
                            .line(error.getLine()).build())
                );
            }

            foTransactionHeaderRepository.save(header);

            foundationService.othersToFoundationKafkaUpdate(header, null);
        }
    }

    @KafkaListener(topics = "#{'${app.kafka.topic.authorize-upload}_${channel-id}'}", groupId = "#{'${spring.kafka.consumer.group-id-notification}'}", containerFactory = "uploadValidatedInvoiceListener")
    public void uploadValidatedInvoiceListen(AuthorizeUploadData message) throws IOException {
        var foTransactionHeader = foTransactionHeaderRepository.findByFoTransactionHeaderId(message.getFoTransactionHeaderId());
        var foTransactionDetail = foTransactionDetailRepository.findAllByFoTransactionHeaderIdOrderByLineNumberAsc(message.getFoTransactionHeaderId());
        var filename = foTransactionHeader.getFileName();
        var formatter = DateTimeFormatter.ofPattern(Constant.FORMAT_DATE);
        var file = File.createTempFile(filename, Constant.EXTENSION);
        var printStream = new PrintStream(file);
        var transactionType = foTransactionHeader.getTransactionType().equalsIgnoreCase(ActionEnum.ADD.name()) ? Constant.TYPE_ADD_IDN : Constant.TYPE_DELETE_IDN;

        printStream.print("0|" + transactionType + "|||" + foTransactionHeader.getCorporateCode() + "|" + (!CommonUtil.isNullOrEmpty(foTransactionHeader.getFileHeaderId()) ? foTransactionHeader.getFileHeaderId() : "") + "|||||||||||||||||" + "\n");

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
        body.add("reference-number", foTransactionHeader.getReferenceNumber());

        var response = uploadInvoiceClient.uploadValidatedInvoice(message.getUser(), channelId, body).getBody();

        if(!Objects.isNull(response) && !response.getErrorCode().equalsIgnoreCase(successCode)){
            foTransactionHeader.setStatus(StatusEnum.FAILED);
            foTransactionHeader.setReason(response.getErrorMessageEn());

            if(response.getErrorCode().equalsIgnoreCase(errorCutoffCode)){
                var totalRecords = foTransactionDetail.size() + 1;

                foInvoiceErrorDetailRepository.save(FoInvoiceErrorDetailEntity.builder()
                        .foInvoiceErrorDetailId(CommonUtil.uuid())
                        .errorCode(response.getErrorCode())
                        .errorDescriptionEng(response.getErrorMessageEn())
                        .errorDescriptionInd(response.getErrorMessageInd())
                        .chainingId(foTransactionHeader.getChainingId())
                        .line(IntStream.range(1, totalRecords).boxed().map(String::valueOf).collect(Collectors.joining(","))).build());
            }
        }

        foundationService.othersToFoundationKafkaUpdate(foTransactionHeader, message.getUser());
    }
}