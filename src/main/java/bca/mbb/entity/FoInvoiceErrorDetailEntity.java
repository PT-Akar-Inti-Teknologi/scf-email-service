package bca.mbb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
@Table(name = "FO_INVOICE_ERROR_DETAIL")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoInvoiceErrorDetailEntity {
    @Id
    @Column(name = "FO_INVOICE_ERROR_DETAIL_ID", nullable = false, length = 32)
    private String foInvoiceErrorDetailId;

    @Column(name = "CHAINING_ID", nullable = false, length = 32)
    private String chainingId;

    @Column(name = "LINE", nullable = false, length = 4000)
    private String line;

    @Column(name = "ERROR_CODE", nullable = false, length = 15)
    private String errorCode;

    @PastOrPresent
    @JsonProperty(value = "created_date")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "ERROR_DESCRIPTION_ENG", length = 100)
    private String errorDescriptionEng;

    @Column(name = "ERROR_DESCRIPTION_IND", length = 100)
    private String errorDescriptionInd;
}
