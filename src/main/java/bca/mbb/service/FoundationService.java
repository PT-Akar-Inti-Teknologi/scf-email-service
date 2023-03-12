package bca.mbb.service;

import bca.mbb.api.MessagingService;
import bca.mbb.clients.FoundationExternalClient;
import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.Constant;
import bca.mbb.dto.foundation.FoundationKafkaBulkUpdateDto;
import bca.mbb.dto.foundation.UserDetailsDto;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.TransactionBulk;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
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

            messagingService.sendMessage(othersToFoundation, TransactionBulk.newBuilder()
                    .setTransactionJSON(mapper.writeValueAsString(FoundationKafkaBulkUpdateDto.builder().corpId(foTransactionHeader.getCorporateCode())
                            .userId(userId)
                            .transactionType(Constant.LOAN_UPLOAD_INVOICE)
                            .streamTransactionId(foTransactionHeader.getChainingId())
                            .transactionAmount(foTransactionHeader.getTotalAmount())
                            .transactionCurrency(CommonUtil.isNullOrEmpty(currency) ? null : currency)
                            .transactionStatus(foTransactionHeader.getStatus().name())
                            .transactionDetails((foTransactionHeader.getTransactionType().equalsIgnoreCase(ActionEnum.ADD.name()) ? Constant.TYPE_ADD_IDN : Constant.TYPE_DELETE_IDN) +" – " + foTransactionHeader.getRemarks())
                            .transactionEffectiveDate(foTransactionHeader.getEffectiveDate())
                            .rejectCancelReason(foTransactionHeader.getReason()).build()))
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
