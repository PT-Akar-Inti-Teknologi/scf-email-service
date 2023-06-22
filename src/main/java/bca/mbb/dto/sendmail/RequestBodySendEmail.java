package bca.mbb.dto.sendmail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestBodySendEmail {
    String transactionType;
    String streamTransactionCode;
    boolean success;
    boolean single;
    List<Map<String,String>> principal;
    List<Map<String,String>> counterparty;
}
