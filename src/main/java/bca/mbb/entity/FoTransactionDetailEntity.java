package bca.mbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "FO_TRANSACTION_DETAIL")
public class FoTransactionDetailEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "FO_TRANSACTION_DETAIL_ID", nullable = false, length = 32)
    private String foTransactionDetailId;

    @Column(name = "FO_TRANSACTION_HEADER_ID", nullable = false, length = 32)
    private String foTransactionHeaderId;

    @Column(name = "TRANSACTION_DETAIL_ID", nullable = false, length = 32)
    private String transactionDetailId;

    @Column(name = "TRANSACTION_HEADER_ID", nullable = false, length = 32)
    private String transactionHeaderId;

    @Column(name = "INVOICE_MASTER_ID", length = 32)
    private String invoiceMasterId;

    @Column(name = "INVOICE_NUMBER", length = 20)
    private String invoiceNumber;

    @Column(name = "INVOICE_DATE")
    private LocalDate invoiceDate;

    @Column(name = "INVOICE_DUE_DATE")
    private LocalDate invoiceDueDate;

    @Column(name = "INVOICE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal invoiceAmount;

    @Column(name = "PRODUCT_CODE", length = 6)
    private String productCode;

    @Column(name = "PROGRAM_CODE", length = 32)
    private String programCode;

    @Column(name = "SELLER_CODE", length = 32)
    private String sellerCode;

    @Column(name = "BUYER_CODE", length = 32)
    private String buyerCode;

    @Column(name = "REMARKS", length = 100)
    private String remarks;

    @Column(name = "LINE_NUMBER")
    private Integer lineNumber;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "STATUS", length = 25)
    private String status;

    @Column(name = "REASON", length = 4000)
    private String reason;

    @Column(name = "FINANCE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal financeAmount;

    @Column(name = "NOTE_NUMBER", length = 20)
    private String noteNumber;

    @Column(name = "FINANCE_DATE")
    private LocalDate financeDate;

    @Column(name = "FINANCE_DUE_DATE")
    private LocalDate financeDueDate;

    @Column(name = "REPAYMENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal repaymentAmount;

    @Column(name = "SETTLEMENT_TYPE", length = 1)
    private String settlementType;

    @Column(name = "INVOICE_UPLOAD_DATE")
    private LocalDate invoiceUploadDate;

    @Column(name = "SELLER_NAME", length = 40)
    private String sellerName;

    @Column(name = "BUYER_NAME", length = 40)
    private String buyerName;

    @Column(name = "NEW_INVOICE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal newInvoiceAmount;

    @Column(name = "NEW_INVOICE_DATE")
    private LocalDate newInvoiceDate;

    @Column(name = "NEW_INVOICE_DUE_DATE")
    private LocalDate newInvoiceDueDate;

    @Column(name = "NEW_REMARKS", length = 100)
    private String newRemarks;

    @Column(name = "OLD_REQUEST_FINANCE_STATUS", length = 25)
    private String oldRequestFinanceStatus;

    @Column(name = "NEW_REQUEST_FINANCE_STATUS", length = 25)
    private String newRequestFinanceStatus;

    @Column(name = "OLD_INVOICE_STATUS", length = 25)
    private String oldInvoiceStatus;

    @Column(name = "NEW_INVOICE_STATUS", length = 25)
    private String newInvoiceStatus;

    @Column(name = "REFERENCE_NUMBER", length = 32)
    private String referenceNumber;

    @Column(name = "CANCELLED_BY", length = 32)
    private String cancelledBy;

    @Column(name = "CANCELLED_DATE")
    private LocalDateTime cancelledDate;

    @PastOrPresent
    @JsonProperty(value = "created_date")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "PAYMENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "OLD_INVOICE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal oldInvoiceAmount;

    @Column(name = "OLD_INVOICE_DATE")
    private LocalDate oldInvoiceDate;

    @Column(name = "OLD_INVOICE_DUE_DATE")
    private LocalDate oldInvoiceDueDate;

    @Column(name = "OLD_REMARKS", length = 100)
    private String oldRemarks;

    @Column(name = "REQUEST_FINANCE_STATUS", length = 25)
    private String requestFinanceStatus;

    @Column(name = "INVOICE_STATUS", length = 25)
    private String invoiceStatus;

    @Column(name = "LOAN_ACCOUNT_NUMBER", length = 20)
    private String loanAccountNumber;

    @Column(name = "COMMITMENT_NUMBER", length = 3)
    private String commitmentNumber;

    @Column(name = "EFFECTIVE_DATE")
    private LocalDate effectiveDate;
}
