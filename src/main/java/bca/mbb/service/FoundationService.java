package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.FoundationExternalClient;
import bca.mbb.config.ApplicationConfiguration;
import bca.mbb.dto.foundation.FoundationKafkaBulkUpdateDto;
import bca.mbb.dto.foundation.UserDetailsDto;
import bca.mbb.entity.FoTransactionHeaderEntity;
import bca.mbb.enums.ActionEnum;
import bca.mbb.enums.StatusEnum;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class FoundationService {

    @Value("${app.kafka.topic.others-to-foundation-bulk}")
    private String othersToFoundation;
    private final FoTransactionHeaderRepository foTransactionHeaderRepository;
    private final FoTransactionDetailRepository foTransactionDetailRepository;
    private final FoundationExternalClient foundationExternalClient;
    private final MessagingService<SpecificRecordBase> messagingService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final ApplicationConfiguration appConfiguration;

    public void othersToFoundationKafkaUpdate(FoTransactionHeaderEntity foTransactionHeader, String userId) {
        try {
            var currency = foTransactionDetailRepository.getCurrencyByFoTransactionId(foTransactionHeader.getFoTransactionHeaderId());

            messagingService.sendMessage(othersToFoundation, TransactionBulk.newBuilder()
                    .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaBulkUpdateDto.builder().corpId(foTransactionHeader.getCorporateCode())
                            .userId(userId)
                            .transactionType(appConfiguration.LOAN_UPLOAD_INVOICE)
                            .streamTransactionId(foTransactionHeader.getChainingId())
                            .transactionAmount(foTransactionHeader.getTotalAmount())
                            .transactionCurrency(CommonUtil.isNullOrEmpty(currency) ? null : currency)
                            .transactionStatus(foTransactionHeader.getStatus().name())
                            .transactionDetails((foTransactionHeader.getTransactionType().equalsIgnoreCase(ActionEnum.ADD.name()) ? appConfiguration.add_description : appConfiguration.delete_description) +" – " + foTransactionHeader.getRemarks())
                            .transactionEffectiveDate(foTransactionHeader.getEffectiveDate())
                            .rejectCancelReason(foTransactionHeader.getReason()).build()))
                    .build());
            if (!foTransactionHeader.getWorkflowFailure().equals(null)) {
                foTransactionHeader.setWorkflowFailure(null);
                foTransactionHeaderRepository.save(foTransactionHeader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            foTransactionHeader.setWorkflowFailure(StatusEnum.UPDATE);
            foTransactionHeader.setUpdatedDate(LocalDateTime.now());
            foTransactionHeaderRepository.save(foTransactionHeader);
        }
    }

    public void setAuthorizedFoundation(UserDetailsDto userDetailsDto, List<String> transactionStreamIds) throws JsonProcessingException {
        foundationExternalClient.transactionDetailHistoryAuthorize(mapper.writeValueAsString(userDetailsDto), mapper.writeValueAsString(transactionStreamIds));
    }
}
