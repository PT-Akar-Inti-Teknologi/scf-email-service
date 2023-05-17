package bca.mbb.enums.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailEnum {

    UPLOAD_INVOICE_TANGGALTRANSAKSI("tanggalTransaksi", "param_transaction_date_id", "param_transaction_date_en"),
    UPLOAD_INVOICE_STATUS("status", "Status", "Status"),
    UPLOAD_INVOICE_TOTALNOMINAL("totalNominal", "param_total_amount", "param_total_amount"),
    UPLOAD_INVOICE_TOTALRECORD("totalRecord", "param_total_record", "param_total_record"),
    UPLOAD_INVOICE_FILETRANSAKSI("fileTransaksi", "param_file_name", "param_file_name"),
    UPLOAD_INVOICE_TIPEUPLOAD("tipeUpload", "param_upload_type_id", "param_upload_type_en"),
    UPLOAD_INVOICE_KETERANGAN("keterangan", "param_remarks", "param_remarks"),
    UPLOAD_INVOICE_NOREFERENSI("noReferensi", "param_ref_no", "param_ref_no"),
    UPLOAD_INVOICE_CURRENCY("currency", "param_currency", "param_currency"),
    UPLOAD_INVOICE_CORPNAME("corpName", "param_corp_name", "param_corp_name"),
    UPLOAD_INVOICE_REASON("reason", "param_reason_id", "param_reason_en");

    private String fieldName;
    private String fieldValueEng;
    private String fieldValueInd;

    public static EmailEnum getPrefixEnum(String name){
        for(EmailEnum email : EmailEnum.values()){
            if(email.name().equals(name)){
                return email;
            }
        }
        return null;
    }

}
