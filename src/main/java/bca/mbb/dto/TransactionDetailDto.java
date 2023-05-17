package bca.mbb.dto;

import bca.mbb.util.Constant;
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
public class TransactionDetailDto {

    private String transactionDetailId;
    private String transactionHeaderId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate effectiveDate;
    private String invoiceMasterId;
    private String invoiceNumber;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate invoiceDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate invoiceDueDate;
    private BigDecimal invoiceAmount;
    private BigDecimal transactionAmount;
    private String mainframeReferenceNumber;
    private String channelReferenceNumber;
    private String status;
    private String wsId;
    private String cisNumber;
    private String loanAccountNumber;
    private String commitmentNumber;
    private String blockId;
    private String productCode;
    private String programCode;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate financeStartDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate financeDueDate;
    private Integer tenorValue;
    private String tenorUnit;
    private String financeCurrency;
    private BigDecimal financeAmount;
    private Integer interestCalculation;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDate pastDueDate;
    private String primaryCreditAccountNumber;
    private String primaryCreditCurrency;
    private BigDecimal primaryCreditAmount;
    private String secondaryCreditAccountNumber;
    private String secondaryCreditCurrency;
    private BigDecimal secondaryCreditAmount;
    private String chargesCode;
    private String chargeCurrency;
    private BigDecimal chargeAmount;
    private String chargeDebitAccountNumber;
    private String interestCurrency;
    private BigDecimal interestAmount;
    private String intDebitAccountNumber;
    private String indexCode;
    private BigDecimal interestBaseRate;
    private Double marginValue;
    private String interestType;
    private String limitType;
    private String failedReason;
    private String noteNumber;
    private String buyerCode;
    private String sellerCode;
    private Integer lineNumber;
    private String remarks;
    private String currency;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE_TIME, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDateTime createdDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = Constant.FORMAT_ENTITY_DATE_TIME, timezone = Constant.FORMAT_ENTITY_TIME_ZONE)
    private LocalDateTime updatedDate;
    private String settlementType;

    private String transactionReferenceNumber;
}
