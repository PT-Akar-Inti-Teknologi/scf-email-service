package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.mapper.FoundationKafkaMapper;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public FoTransactionHeaderEntity othersToFoundationKafkaUpdate(FoTransactionHeaderEntity foTransactionHeader, String userId) {
        try {
            var currency = foTransactionDetailRepository.getCurrencyByFoTransactionId(foTransactionHeader.getFoTransactionHeaderId());

            log.info("Hit foundation update");
            log.info("foTransactionHeader : {}", foTransactionHeader.toString());
            log.info("kafka message : {}", mapper.writeValueAsString(FoundationKafkaMapper.INSTANCE.from(userId, foTransactionHeader, currency)));

            messagingService.sendMessage(othersToFoundation, TransactionBulk.newBuilder()
                    .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaMapper.INSTANCE.from(userId, foTransactionHeader, currency)))
                    .build());

            if (!Objects.isNull(foTransactionHeader.getWorkflowFailure())) {
                foTransactionHeader.setWorkflowFailure(null);
                foTransactionHeaderRepository.save(foTransactionHeader);
            }
        } catch (Exception e) {
            foTransactionHeader.setWorkflowFailure(StatusEnum.UPDATE);
            foTransactionHeader.setUpdatedDate(LocalDateTime.now());
            foTransactionHeaderRepository.save(foTransactionHeader);
        }
        return foTransactionHeader;
    }
}
