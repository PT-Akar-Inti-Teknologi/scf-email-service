package bca.mbb.enums.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionPrefixEnum {

    UPLOAD_INVOICE ("UPLOAD_INVOICE", "UIN");

    private String transactionName;
    private String prefix;

    public static TransactionPrefixEnum getPrefix (String transactionName) {
        for(TransactionPrefixEnum prefix : TransactionPrefixEnum.values()){
            if(prefix.getTransactionName().equals(transactionName)){
                return prefix;
            }
        }
        return null;
    }
}
