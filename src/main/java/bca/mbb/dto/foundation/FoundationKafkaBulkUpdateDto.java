package bca.mbb.dto.foundation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FoundationKafkaBulkUpdateDto extends FoundationKafkaDto {
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String rejectCancelReason;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Jakarta")
    private LocalDate transactionEffectiveDate;
}
