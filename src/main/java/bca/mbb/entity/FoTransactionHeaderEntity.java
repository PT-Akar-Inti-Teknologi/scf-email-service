package bca.mbb.entity;

import bca.mbb.enums.StatusEnum;
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
@Table(name = "FO_TRANSACTION_HEADER")
public class FoTransactionHeaderEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "FO_TRANSACTION_HEADER_ID", nullable = false, length = 32)
    private String foTransactionHeaderId;

    @Column(name = "TRANSACTION_HEADER_ID", nullable = false, length = 32)
    private String transactionHeaderId;

    @Column(name = "CHAINING_ID", length = 32)
    private String chainingId;

    @Column(name = "REFERENCE_NUMBER", length = 32)
    private String referenceNumber;

    @Column(name = "EFFECTIVE_DATE")
    private LocalDate effectiveDate;

    @Column(name = "PRIMARY_PARTY_TYPE", length = 12)
    private String primaryPartyType;

    @Column(name = "PRIMARY_PARTY_CODE", length = 17)
    private String primaryPartyCode;

    @Column(name = "PRIMARY_PARTY_NAME", length = 40)
    private String primaryPartyName;

    @Column(name = "PRIMARY_PARTY_ROLE", length = 40)
    private String primaryPartyRole;

    @Column(name = "SECONDARY_PARTY_TYPE", length = 12)
    private String secondaryPartyType;

    @Column(name = "SECONDARY_PARTY_CODE", length = 17)
    private String secondaryPartyCode;

    @Column(name = "SECONDARY_PARTY_NAME", length = 40)
    private String secondaryPartyName;

    @Column(name = "SECONDARY_PARTY_ROLE", length = 40)
    private String secondaryPartyRole;

    @Column(name = "TRANSACTION_NAME", length = 32)
    private String transactionName;

    @Column(name = "TRANSACTION_TYPE", length = 32)
    private String transactionType;

    @Column(name = "FILE_NAME", length = 50)
    private String fileName;

    @Column(name = "FILE_TYPE", length = 5)
    private String fileType;

    @Column(name = "REMARK", length = 100)
    private String remarks;

    @Column(name = "TOTAL_RECORD")
    private Integer totalRecord;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "REASON", length = 4000)
    private String reason;

    @Column(name = "STATUS", length = 25)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "PROGRAM_CODE", length = 32)
    private String programCode;

    @Column(name = "PROGRAM_NAME", length = 40)
    private String programName;

    @Column(name = "TOTAL_SUCCESSFUL_RECORD")
    private Integer totalSuccessfulRecord;

    @Column(name = "TOTAL_SUCCESSFUL_AMOUNT", precision = 5, scale = 2)
    private BigDecimal totalSuccessfulAmount;

    @Column(name = "TOTAL_FAILED_RECORD")
    private Integer totalFailedRecord;

    @Column(name = "TOTAL_FAILED_AMOUNT", precision = 5, scale = 2)
    private BigDecimal totalFailedAmount;

    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Column(name = "FINANCE_DUE_DATE")
    private LocalDate financeDueDate;

    @Column(name = "FINANCE_TENOR")
    private Integer financeTenor;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "ACCOUNT_NUMBER", length = 32)
    private String accountNumber;

    @Column(name = "ACCOUNT_NAME", length = 100)
    private String accountName;

    @Column(name = "TOTAL_ACCOUNT_TRANSFER_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAccountTransferAmount;

    @Column(name = "IS_AGREE", length = 10)
    private String isAgree;

    @Column(name = "CREDIT_NOTES", length = 4000)
    private String creditNotes;

    @Column(name = "TOTAL_CREDIT_NOTE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalCreditNoteAmount;

    @Column(name = "REQUESTED_BY", length = 50)
    private String requestedBy;

    @Column(name = "AUTHORIZED_BY", length = 50)
    private String authorizedBy;

    @Column(name = "REQUESTED_DATE")
    private LocalDateTime requestedDate;

    @Column(name = "AUTHORIZED_DATE")
    private LocalDateTime authorizedDate;

    @PastOrPresent
    @JsonProperty(value = "created_date")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name="ASYNCRONOUS_TRANSFER", columnDefinition="varchar2(3) default 'NO'", length = 3)
    private String asyncronousTransfer;

    @Column(name = "CORPORATE_CODE")
    private String corporateCode;

    @Column(name = "FILE_HEADER_ID")
    private String fileHeaderId;

    @Column(name = "INPUT_TENOR_BY_USER")
    private String inputTenorByUser;

    @PrePersist
    private void onCreate() {
        if (this.getAsyncronousTransfer() == null) {
            this.setAsyncronousTransfer("NO");
        }
        this.createdDate = LocalDateTime.now();
    }
}
