package bca.mbb.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public interface FoTransactionHeadersDto {

    String getDescription();
    String getTransactionName();
    BigDecimal getTotalAmount();
    LocalDate getEffectiveDate();


}
