package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.FoundationExternalClient;
import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.foundation.UserDetailsDto;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import org.apache.avro.specific.SpecificRecordBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@SuppressWarnings("all")
class FoundationServiceTest {

    @Autowired
    private FoundationService foundationService;

    @MockBean
    private FoTransactionDetailRepository foTransactionDetailRepository;

    @MockBean
    private FoTransactionHeaderRepository foTransactionHeaderRepository;

    @MockBean
    private MessagingService<SpecificRecordBase> messagingService;

    @MockBean
    private FoundationExternalClient foundationExternalClient;

    private FoTransactionHeaderEntity getFoTransactionHeaderEntity(ActionEnum transactionType, StatusEnum workflowFailure, StatusEnum status) {
        FoTransactionHeaderEntity foTransactionHeader = new FoTransactionHeaderEntity();
        foTransactionHeader.setTransactionType(transactionType.name());
        foTransactionHeader.setWorkflowFailure(workflowFailure);
        foTransactionHeader.setStatus(status);
        return foTransactionHeader;
    }

    @Test
    void othersToFoundationKafkaUpdate() {
        when(foTransactionDetailRepository.getCurrencyByFoTransactionId(any()))
                .thenReturn(null)
                .thenReturn(null)
                .thenReturn("IDR");
        assertTrue(!ObjectUtils.isEmpty(foundationService.othersToFoundationKafkaUpdate(getFoTransactionHeaderEntity(ActionEnum.ADD, null, null), "farah")));
        assertTrue(!ObjectUtils.isEmpty(foundationService.othersToFoundationKafkaUpdate(getFoTransactionHeaderEntity(ActionEnum.ADD, null, StatusEnum.DONE), "farah")));
        assertTrue(!ObjectUtils.isEmpty(foundationService.othersToFoundationKafkaUpdate(getFoTransactionHeaderEntity(ActionEnum.DELETE, StatusEnum.CREATE, StatusEnum.DONE), "farah")));
    }

    @Test
    void setAuthorizedFoundation() throws JsonProcessingException {
        when(foundationExternalClient.transactionDetailHistoryAuthorize(any(), any())).thenReturn(new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK));
        assertTrue(foundationService.setAuthorizedFoundation(new UserDetailsDto(), new ArrayList<>()).getBody().getErrorCode().equals("SCF-00-000"));
    }
}