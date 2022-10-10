package bca.mbb.dto.foundation;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FoundationKafkaDto {
    private String corpId;
    private String userId;
    private String streamTransactionId;
    private String transactionType;
    private String transactionStatus;
    private String transactionDetails;
}
