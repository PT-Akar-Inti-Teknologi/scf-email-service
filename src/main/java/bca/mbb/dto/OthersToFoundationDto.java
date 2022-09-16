package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OthersToFoundationDto {
    private String corpId;
    private String userId;
    private String streamTransactionId;
    private String transactionType;
    private String transactionStatus;
    private String transactionDetails;
    private String transactionAmount;
    private String transactionCurrency;
    private String rejectCancelReason;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Jakarta")
    private LocalDate transactionEffectiveDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime uploadDate;
}
