package bca.mbb.dto.sendmail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestBodySendBodyEmail {
    private String streamTransactionCode;
    private String transactionType;
    private String type;
    private String channelId;
    private String userId;
    private boolean success;
    private boolean single;
    private List<Map<String,String>>principal;
    private List<Map<String,String>>counterparty;
    private String currency;
    private String corpId;
    private String[] emailCorporates;
    private String[] emailUser;
    private String typePayment;
    private boolean dotaaa;

}
