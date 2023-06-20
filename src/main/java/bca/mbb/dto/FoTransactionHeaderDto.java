package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lib.fo.entity.FoTransactionHeaderEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class FoTransactionHeaderDto {
    private String counterpartyName;
    private String counterpartyCode;

    public FoTransactionHeaderDto(FoTransactionHeaderEntity foTransactionHeaderEntity) {
        this.counterpartyName = foTransactionHeaderEntity.getSecondaryPartyName();
        this.counterpartyCode = foTransactionHeaderEntity.getSecondaryPartyCode();

        if (foTransactionHeaderEntity.getPrimaryPartyType().equals("COUNTERPARTY")) {
            this.counterpartyName = foTransactionHeaderEntity.getPrimaryPartyName();
            this.counterpartyCode = foTransactionHeaderEntity.getPrimaryPartyCode();
        }
    }
}
