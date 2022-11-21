package bca.mbb.dto.foundation;

import bca.mbb.dto.Constant;
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
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate transactionEffectiveDate;
}
