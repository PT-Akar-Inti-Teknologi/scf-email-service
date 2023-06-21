package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lib.fo.entity.FoTransactionHeaderEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class FoTransactionHeaderDto {
    private String counterpartyName;
    private String counterpartyCode;
    private Long totalInvoiceSuccess;
    private Long totalInvoiceFailed;
    private Long totalInvoice;
    private BigDecimal totalPaymentSuccess;
    private BigDecimal totalPaymentFailed;

    public FoTransactionHeaderDto(FoTransactionHeaderEntity foTransactionHeaderEntity) {
        this.counterpartyName = foTransactionHeaderEntity.getSecondaryPartyName();
        this.counterpartyCode = foTransactionHeaderEntity.getSecondaryPartyCode();

        if (foTransactionHeaderEntity.getPrimaryPartyType().equals("COUNTERPARTY")) {
            this.counterpartyName = foTransactionHeaderEntity.getPrimaryPartyName();
            this.counterpartyCode = foTransactionHeaderEntity.getPrimaryPartyCode();
        }
    }

    public FoTransactionHeaderDto(Long totalInvoiceSuccess, Long totalInvoiceFailed, Long totalInvoice, BigDecimal totalPaymentSuccess, BigDecimal totalPaymentFailed) {
        this.totalInvoiceSuccess = totalInvoiceSuccess;
        this.totalInvoiceFailed = totalInvoiceFailed;
        this.totalInvoice = totalInvoice;
        this.totalPaymentSuccess = totalPaymentSuccess;
        this.totalPaymentFailed = totalPaymentFailed;

    }
}
