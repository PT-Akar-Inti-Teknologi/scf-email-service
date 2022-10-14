package bca.mbb.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    public static final String ERROR_HAS_BEEN_AUTHORIZED = "TRANSACTION HAS BEEN AUTHORIZED BY ANOTHER USER";
    public static final String MBB_SUCCESS_CODE = "MBB-00-000";

    public static final String MBB_AUTHORIZE_CODE = "MBB-00-001";
    public static final String MBB_REJECTED_CODE = "MBB-00-002";
    public static final String MBB_ERROR_CODE = "MBB-00-999";
    public static final String ERROR_CHALLENGE_CODE_EN = "Silahkan memasukkan Key Response Appli 2 yang sesuai dari KeyBCA Anda";
    public static final String ERROR_CHALLENGE_CODE_IND = "Please input the correct Appli 2 Key Response from your KeyBCA";
    public static final String OTHERS_TO_FOUNDATION_BULK_CREATE = "BULK_CREATE";
    public static final String TRANSACTION_TYPE_ADD = "ADD";
    public static final String pattern_dateTime = "dd/MM/yyyy HH:mm:ss";
    public static final String pattern_dateTime_prefix = "yyMMddHHmmss";
    public static final String LOAN_UPLOAD_INVOICE = "LOAN_UPLOAD_INVOICE";
    public static final String OTHERS_TO_FOUNDATION_BULK_UPDATE = "BULK_UPDATE";
    public static final String pattern_dateTime_foundation= "dd MMM yyyy HH:mm:ss";
    public static final String pattern_date_foundation= "YYYY-MM-DD";
    public static final String delete_description = "Tambah";
    public static final String add_description = "Hapus";
}
