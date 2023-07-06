package bca.mbb.util;

import bca.mbb.enums.email.EmailEnum;

import java.util.ArrayList;
import java.util.List;

import static bca.mbb.enums.email.EmailEnum.*;

public class ConstantEmail {

    private ConstantEmail() {
    }

    public static List<EmailEnum> PRINCIPAL_LIST_PAY_INVOICE = new ArrayList<>(List.of(new EmailEnum[]{
            PROGRAMCODE,
            PROGRAMNAME,
            DEALERNAME,
            TANGGALTRANSAKSI,
            TOTALNOMINAL,
            TOTALRECORD,
            KETERANGAN,
            NOREFERENSI,
            CURRENCY,
    }));

    public static List<EmailEnum> COUNTERPARTY_LIST_PAY_INVOICE = new ArrayList<>(List.of(new EmailEnum[]{
            TANGGALTRANSAKSI,
            TOTALNOMINAL,
            TOTALSUCEEDAMOUNT,
            TOTALSUCEEDRECORD,
            TOTALFAILEDAMOUNT,
            TOTALFAILEDRECORD,
            PROGRAMCODE,
            PROGRAMNAME,
            DEALERNAME,
            DEALERCODE,
            CURRENCY,
            KETERANGAN,
            NOREFERENSI,
            ACCOUNT,
            ACCOUNTAMOUNT,
            CREDITNOTE,
            CREDITNOTEAMOUT
    }));

    public static List<EmailEnum> PRINCIPAL_LIST_UPLOAD_LIST = new ArrayList<>(List.of(new EmailEnum[]{
            TANGGALTRANSAKSI,
            STATUS,
            TOTALNOMINAL,
            TOTALRECORD,
            FILETRANSAKSI,
            TIPEUPLOAD,
            KETERANGAN,
            NOREFERENSI,
            CURRENCY,
            REASON
    }));

    public static List<EmailEnum> COUNTER_PARTY_LIST_UPLOAD_INVOICE = new ArrayList<>(List.of(new EmailEnum[]{
            CORPNAME,
            TANGGALTRANSAKSI,
            TOTALNOMINAL,
            TOTALRECORD,
            TIPEUPLOAD,
            CURRENCY,
            KETERANGAN,
            NOREFERENSI
    }));



}
