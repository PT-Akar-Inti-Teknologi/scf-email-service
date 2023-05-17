package bca.mbb.service;

import bca.mbb.config.KafkaProducerResponse;
import bca.mbb.config.SCFKafkaProducer;
import bca.mbb.dto.foundation.FoundationKafkaBulkUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lib.fo.enums.StatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@SuppressWarnings("all")
class MessagingServiceImplTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessagingServiceImpl messagingService;

    @MockBean
    private SCFKafkaProducer<Object> producer;

    @Test
    void setMessagingService() throws JsonProcessingException {
        when(producer.sendMessage(anyString(), any())).thenReturn(new KafkaProducerResponse(true, null, null));
        assertTrue(messagingService.sendMessage("othersToFoundation", TransactionBulk.newBuilder()
                .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaBulkUpdateDto.builder()
                        .corpId("coorporateCode")
                        .userId("userId")
                        .transactionType(Constant.LOAN_UPLOAD_INVOICE)
                        .streamTransactionId("chainingId")
                        .transactionAmount(BigDecimal.valueOf(0))
                        .transactionCurrency("currency")
                        .transactionStatus(StatusEnum.CREATE.name())
                        .transactionDetails("remarks")
                        .transactionEffectiveDate(LocalDate.now())
                        .rejectCancelReason("reason").build()))
                .build()).isSuccess());
    }
}