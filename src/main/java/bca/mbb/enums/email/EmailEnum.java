package bca.mbb.enums.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailEnum {

    TANGGALTRANSAKSI("tanggalTransaksi", "param_transaction_date_id", "param_transaction_date_en"),
    STATUS("status", "param_status_id", "param_status_en"),
    TOTALNOMINAL("totalNominal", "param_total_amount", "param_total_amount"),
    TOTALRECORD("totalRecord", "param_total_record", "param_total_record"),
    FILETRANSAKSI("fileTransaksi", "param_file_name", "param_file_name"),
    TIPEUPLOAD("tipeUpload", "param_upload_type_id", "param_upload_type_en"),
    KETERANGAN("keterangan", "param_remarks", "param_remarks"),
    NOREFERENSI("noReferensi", "param_ref_no", "param_ref_no"),
    CURRENCY("currency", "param_currency", "param_currency"),
    CORPNAME("corpName", "param_corp_name", "param_corp_name"),
    REASON("reason", "param_reason_id", "param_reason_en"),
    PROGRAMNAME("programName", "param_program_name", "param_program_name"),
    PROGRAMCODE("programCode", "param_program_code", "param_program_code"),
    DEALERNAME("dealerName", "param_dealer_name", "param_dealer_name"),
    DEALERCODE("dealerCode", "param_dealer_code", "param_dealer_code"),
    TRANSACTIONDATE("transactionDate", "transaction_date_id", "transaction_date_en"),
    TOTALSUCEEDAMOUNT("totalSucceedAmount", "param_total_succeed_amount", "param_total_succeed_amount"),
    TOTALSUCEEDRECORD("totalSucceedRecord", "param_total_succeed_record", "param_total_succeed_record"),
    TOTALFAILEDAMOUNT("totalFailedAmount", "param_total_failed_amount", "param_total_failed_amount"),
    TOTALFAILEDRECORD("totalFailedRecord", "param_total_failed_record", "param_total_failed_record"),
    ACCOUNT("accountNumber", "param_account", "param_account"),
    ACCOUNTAMOUNT("accountAmount", "param_account_amount", "param_account_amount"),
    CREDITNOTE("creditNoted", "param_credit_note", "param_credit_note"),
    CREDITNOTEAMOUT("creditNotedAmount", "param_credit_note_amount", "param_credit_note_amount");

    private String fieldName;
    private String fieldValueInd;
    private String fieldValueEng;

    public static EmailEnum getPrefixEnum(String name){
        for(EmailEnum email : EmailEnum.values()){
            if(email.name().equals(name)){
                return email;
            }
        }
        return null;
    }

}
