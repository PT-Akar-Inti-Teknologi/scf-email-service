package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApprovalBulkDto {
    @JsonProperty("corpId")
    private String corpId;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("streamTransactionId")
    private String streamTransactionId;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("transactionStatus")
    private String transactionStatus;
    @JsonProperty("newTransactionEffectiveDate")
    private String newTransactionEffectiveDate;
    @JsonProperty("rejectReason")
    private String rejectReason;
}
