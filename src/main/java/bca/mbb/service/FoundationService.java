package bca.mbb.service;

import bca.mbb.adaptor.UpdateWorkflowFailureService;
import bca.mbb.api.MessagingService;
import bca.mbb.mapper.FoundationKafkaMapper;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.util.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lib.fo.entity.FoTransactionHeaderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class FoundationService {

    @Value("${app.kafka.topic.others-to-foundation-bulk}")
    private String othersToFoundation;
    private final FoTransactionHeaderRepository foTransactionHeaderRepository;
    private final FoTransactionDetailRepository foTransactionDetailRepository;
    private final MessagingService<SpecificRecordBase> messagingService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final UpdateWorkflowFailureService updateWorkflowFailureService;

    public FoTransactionHeaderEntity othersToFoundationKafkaUpdate(FoTransactionHeaderEntity foTransactionHeader, String userId) throws JsonProcessingException {
        var currency = foTransactionDetailRepository.getCurrencyByFoTransactionId(foTransactionHeader.getFoTransactionHeaderId());

        log.info("Hit foundation update");
        log.info("foTransactionHeader : {}", foTransactionHeader.toString());
        log.info("kafka message : {}", mapper.writeValueAsString(FoundationKafkaMapper.INSTANCE.from(userId, foTransactionHeader, currency)));

        var sendKafkaFoundation = messagingService.sendMessage(othersToFoundation, TransactionBulk.newBuilder()
                .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaMapper.INSTANCE.from(userId, foTransactionHeader, currency)))
                .build());

        if (!sendKafkaFoundation.isSuccess()) {
            foTransactionHeader.setWorkflowFailure(Constant.WORKFLOW_FAILURE_UPDATE);
            foTransactionHeader.setUpdatedDate(LocalDateTime.now());
            foTransactionHeaderRepository.save(foTransactionHeader);
            updateWorkflowFailureService.updateWorkflowFailure(foTransactionHeader);
        }

        return foTransactionHeader;
    }
}
