package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OthersToFoundationDto {
    private String corpId;
    private String userId;
    private String streamTransactionId;
    private String transactionType;
    private String transactionStatus;
    private String transactionDetails;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime uploadDate;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String rejectCancelReason;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Jakarta")
    private LocalDate transactionEffectiveDate;
}
