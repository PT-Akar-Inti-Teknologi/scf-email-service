package bca.mbb.dto.sendMail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBodySendEmail {
    String transactionType;
    String streamTransactionCode;
    boolean success;
    boolean single;
    List<Map<String,String>> principal;
    List<Map<String,String>> counterparty;
}
