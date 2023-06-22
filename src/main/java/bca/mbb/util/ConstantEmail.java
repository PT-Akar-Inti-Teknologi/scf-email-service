package bca.mbb.util;

import bca.mbb.enums.email.EmailEnum;

import java.util.ArrayList;
import java.util.List;

import static bca.mbb.enums.email.EmailEnum.*;

public class ConstantEmail {

    private ConstantEmail() {
    }

    public static List<EmailEnum> PRINCIPAL_LIST = new ArrayList<>(List.of(new EmailEnum[]{
            PROGRAMCODE,
            PROGRAMNAME,
            DEALERNAME,
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

    public static List<EmailEnum> COUNTERPARTY_LIST = new ArrayList<>(List.of(new EmailEnum[]{
            STATUS,
            CORPNAME,
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
            TOTALRECORD,
            TIPEUPLOAD,
            CURRENCY,
            KETERANGAN,
            NOREFERENSI,
            ACCOUNT,
            ACCOUNTAMOUNT,
            CREDITNOTE,
            CREDITNOTEAMOUT
    }));



}
