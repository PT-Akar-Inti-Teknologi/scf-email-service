package bca.mbb.util;

import bca.mbb.enums.email.EmailEnum;

import java.util.ArrayList;
import java.util.List;

import static bca.mbb.enums.email.EmailEnum.*;

public class ConstantEmail {

    private ConstantEmail() {
    }

    public static List<EmailEnum> UPLOAD_INVOICE_PRINCIPAL_LIST = new ArrayList<>(List.of(new EmailEnum[]{
            UPLOAD_INVOICE_TANGGALTRANSAKSI, UPLOAD_INVOICE_STATUS, UPLOAD_INVOICE_TOTALNOMINAL,
            UPLOAD_INVOICE_TOTALRECORD,
            UPLOAD_INVOICE_FILETRANSAKSI, UPLOAD_INVOICE_TIPEUPLOAD, UPLOAD_INVOICE_KETERANGAN,
            UPLOAD_INVOICE_NOREFERENSI,
            UPLOAD_INVOICE_CURRENCY,
            UPLOAD_INVOICE_REASON
    }));

    public static List<EmailEnum> UPLOAD_INVOICE_COUNTER_PARTY_LIST = new ArrayList<>(List.of(new EmailEnum[]{
            UPLOAD_INVOICE_CORPNAME,
            UPLOAD_INVOICE_TANGGALTRANSAKSI,
            UPLOAD_INVOICE_TOTALNOMINAL,
            UPLOAD_INVOICE_TOTALRECORD,
            UPLOAD_INVOICE_TIPEUPLOAD,
            UPLOAD_INVOICE_KETERANGAN,
            UPLOAD_INVOICE_NOREFERENSI
    }));



}
