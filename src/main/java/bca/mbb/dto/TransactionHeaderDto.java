package bca.mbb.dto;

import bca.mbb.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHeaderDto {

    private String transactionHeaderId;
    private String chainingId;
    private String wsId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Jakarta")
    private LocalDate effectiveDate;
    private String primaryPartyType;
    private String primaryPartyCode;
    private String primaryPartyName;
    private String primaryPartyRole;
    private String secondaryPartyType;
    private String secondaryPartyCode;
    private String secondaryPartyName;
    private String secondaryPartyRole;
    private String programCode;
    private String programName;
    private String fileName;
    private String fileReference;
    private String fileType;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime fileGenerationDate;
    private String remarks;
    private String channelReferenceNumber;
    private String menuMasterScfId;
    private String transactionName;
    private String channelId;
    private Integer totalRecord;
    private BigDecimal totalAmount;
    private String accountNumber;
    private String accountName;
    private BigDecimal accountTransferAmount;
    private String creditNoteTransactionHeaderId;
    private String failedReason;
    private StatusEnum status;
    private String corporateCode;
    private String transactionType;
    private String asyncronousTransfer;
    private String isInvalid;
    private String executedBy;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime executedDate;
    private String flagAgreement;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime createdDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime updatedDate;
    private String inputTenorByUser;

}
