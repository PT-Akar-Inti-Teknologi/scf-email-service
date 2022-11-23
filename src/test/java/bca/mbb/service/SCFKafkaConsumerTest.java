package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.UploadInvoiceClient;
import bca.mbb.dto.*;
import bca.mbb.repository.FoInvoiceErrorDetailRepository;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.scf.avro.AuthorizeUploadData;
import bca.mbb.scf.avro.NotificationData;
import bca.mbb.scf.avro.TransactionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lib.fo.entity.FoTransactionDetailEntity;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@SuppressWarnings("all")
class SCFKafkaConsumerTest {

    @Autowired
    private SCFKafkaConsumer kafkaConsumer;

    @MockBean
    private FoTransactionHeaderRepository foTransactionHeaderRepository;

    @MockBean
    private FoInvoiceErrorDetailRepository foInvoiceErrorDetailRepository;

    @MockBean
    private FoundationService foundationService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private FoTransactionDetailRepository foTransactionDetailRepository;

    @MockBean
    private UploadInvoiceClient uploadInvoiceClient;

    @MockBean
    private MessagingService messagingService;

    private NotificationData getNotificationData(String listInvoiceError, StatusEnum status) {
        NotificationData notificationData = new NotificationData();
        notificationData.setChannelId("FOSCF");
        notificationData.setListInvoiceErrorJson(listInvoiceError);
        notificationData.setStatus(status.name());
        return notificationData;
    }

    private FoTransactionHeaderEntity getFoTransactionHeaderEntity(StatusEnum status) {
        FoTransactionHeaderEntity foTransactionHeader = new FoTransactionHeaderEntity();
        foTransactionHeader.setStatus(status);
        foTransactionHeader.setTransactionType("ADD");
        foTransactionHeader.setFileName("test");
        foTransactionHeader.setChainingId("chainingId");
        foTransactionHeader.setTotalRecord(10);
        return foTransactionHeader;
    }

    private InvoiceError getInvoiceError() {
        InvoiceError invoiceError = new InvoiceError();
        invoiceError.setErrorCode("MBB-99-999");
        invoiceError.setErrorDescInd("Duplicate Invoice");
        invoiceError.setErrorDescInd("Duplikat Invoice");
        invoiceError.setLine("1,2,3,4,5");
        return invoiceError;
    }

    private AuthorizeUploadData getAuthorizeUploadData() {
        AuthorizeUploadData authorizeUploadData = new AuthorizeUploadData();
        return authorizeUploadData;
    }

    private FoTransactionDetailEntity getFoTransactionDetailEntity() {
        FoTransactionDetailEntity foTransactionDetail = new FoTransactionDetailEntity();
        foTransactionDetail.setInvoiceDueDate(LocalDate.now());
        foTransactionDetail.setInvoiceDate(LocalDate.now());
        return foTransactionDetail;
    }

    private TransactionData getTransactionData(String transactionType, String entityName, String entityValue) {
        TransactionData transactionData = new TransactionData();
        transactionData.setChannelId("FOSCF");
        transactionData.setTransactionType(transactionType);
        transactionData.setEntityName(entityName);
        transactionData.setEntityValue(entityValue);
        return transactionData;
    }

    private TransactionHeaderDto getTransactionHeaderDto() {
        TransactionHeaderDto transactionHeaderDto = new TransactionHeaderDto();
        transactionHeaderDto.setChannelReferenceNumber("NULL0909090909");
        return transactionHeaderDto;
    }

    private TransactionDetailDto getTransactionDetailDto(String referenceNumber) {
        TransactionDetailDto transactionDetailDto = new TransactionDetailDto();
        transactionDetailDto.setChannelReferenceNumber(referenceNumber);
        transactionDetailDto.setProductCode(Constant.PRODUCT_CODE_REFACT);
        return transactionDetailDto;
    }

    @Test
    void validateDoneListen() throws JsonProcessingException {
        when(foTransactionHeaderRepository.findByChainingIdAndTransactionName(any(), any()))
                .thenReturn(new FoTransactionHeaderEntity());
        kafkaConsumer.validateDoneListen(getNotificationData(null, StatusEnum.SUCCESS));

        kafkaConsumer.validateDoneListen(getNotificationData(mapper.writeValueAsString(List.of(getInvoiceError())), StatusEnum.FAILED));
    }

    @Test
    void uploadValidatedInvoiceListen() throws IOException {
        when(foTransactionHeaderRepository.findByFoTransactionHeaderId(any())).thenReturn(getFoTransactionHeaderEntity(StatusEnum.SUCCESS));
        when(foTransactionDetailRepository.findAllByFoTransactionHeaderIdOrderByLineNumberAsc(any())).thenReturn(List.of(getFoTransactionDetailEntity()));

        when(uploadInvoiceClient.uploadValidatedInvoice(any(), any(), any()))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(new ApiResponse("SCF-00-009", "Cut Off Time", "Waktu Cut Off", null), HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(new ApiResponse("SCF-99-999", "Invalid Error", "Invalid Error", null), HttpStatus.OK));

        kafkaConsumer.uploadValidatedInvoiceListen(getAuthorizeUploadData());
        kafkaConsumer.uploadValidatedInvoiceListen(getAuthorizeUploadData());
        kafkaConsumer.uploadValidatedInvoiceListen(getAuthorizeUploadData());
        kafkaConsumer.uploadValidatedInvoiceListen(getAuthorizeUploadData());
    }

    @Test
    void channelSynchronizerListen() throws JsonProcessingException {
        when(foTransactionHeaderRepository.findByTransactionHeaderId(any()))
                .thenReturn(getFoTransactionHeaderEntity(StatusEnum.SUCCESS))
                .thenReturn(null);
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.REQUEST_FINANCE.name(), Constant.ENTITY_NAME_TRANSACTION_HEADER, mapper.writeValueAsString(getTransactionHeaderDto())));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.RESERVE_LIMIT.name(), Constant.ENTITY_NAME_TRANSACTION_HEADER, mapper.writeValueAsString(getTransactionHeaderDto())));

        when(foTransactionDetailRepository.findByTransactionDetailId(any()))
                .thenReturn(new FoTransactionDetailEntity())
                .thenReturn(new FoTransactionDetailEntity())
                .thenReturn(new FoTransactionDetailEntity())
                .thenReturn(null);
        when(foTransactionHeaderRepository.findByTransactionHeaderId(any())).thenReturn(getFoTransactionHeaderEntity(StatusEnum.DONE));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.RESERVE_LIMIT.name(), Constant.ENTITY_NAME_TRANSACTION_DETAIL, mapper.writeValueAsString(getTransactionDetailDto(Constant.CHANNEL_REFERENCE_NUMBER_REFACT))));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.RESERVE_LIMIT.name(), Constant.ENTITY_NAME_TRANSACTION_DETAIL, mapper.writeValueAsString(getTransactionDetailDto(Constant.CHANNEL_REFERENCE_NUMBER_PAYFIN))));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.RESERVE_LIMIT.name(), Constant.ENTITY_NAME_TRANSACTION_DETAIL, mapper.writeValueAsString(getTransactionDetailDto("RECFIN"))));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.REQUEST_FINANCE.name(), Constant.ENTITY_NAME_TRANSACTION_DETAIL, mapper.writeValueAsString(getTransactionDetailDto("RECFIN"))));
        kafkaConsumer.channelSynchronizerListen(getTransactionData(ActionEnum.UPLOAD_INVOICE.name(), Constant.ENTITY_NAME_TRANSACTION_DETAIL, mapper.writeValueAsString(getTransactionDetailDto("RECFIN"))));
    }
}