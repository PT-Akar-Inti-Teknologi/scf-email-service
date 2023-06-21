package bca.mbb.dto.sendmail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResponseEmailHeaderDto {
    private String transactionSummaryId;
    private String streamTransactionCode;
    private String corporateId;
    private String corpId;
    private String emailAddress;
}
