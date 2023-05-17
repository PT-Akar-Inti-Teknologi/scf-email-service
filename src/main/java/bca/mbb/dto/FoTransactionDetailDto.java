package bca.mbb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class FoTransactionDetailDto {
    private String programCode;
    private String sellerId;
    private String buyerId;

    public FoTransactionDetailDto (String programCode, String sellerId, String buyerId) {
        this.programCode = programCode;
        this.sellerId = sellerId;
        this.buyerId = buyerId;

    }
}
