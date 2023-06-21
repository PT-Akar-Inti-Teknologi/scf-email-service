package bca.mbb.enums.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateCodeEnum {

    UPLOAD_INVOICE_SUCCES("UPLOAD_INVOICE", "Success", "", "NOTIF_EMAIL_SCF_SUCCESS_UPLOAD_PRINCIPAL_MBB"),
    UPLOAD_INVOICE_FAILED("UPLOAD_INVOICE","Failed", "","NOTIF_EMAIL_SCF_FAILED_UPLOAD_PRINCIPAL_MBB"),
    UPLOAD_INVOICE_COUNTERPARTY("UPLOAD_INVOICE","","", "NOTIF_EMAIL_SCF_COUNTERPARTY_UPLOAD_INVOICE_MBB"),

    PAY_INVOICE_PRINCIPAL("PAY_INVOICE","","-", "NOTIF_SCF_PAY_INVOICE_PRINCIPAL"),
    PAY_INVOICE_COUNTERPARTY("PAY_INVOICE","","CN and ACC", "NOTIF_SCF_PAY_INVOICE_CP"),
    PAY_INVOICE_COUNTERPARTY_CN("PAY_INVOICE","","CN", "NOTIF_SCF_PAY_INVOICE_CP_CN"),
    PAY_INVOICE_COUNTERPARTY_ACC("PAY_INVOICE","","ACC", "NOTIF_SCF_PAY_INVOICE_CP_ACC"),
    PAY_INVOICE_COUNTERPARTY_SUCCESS_CN("PAY_INVOICE","","Partially-CN", "NOTIF_SCF_PAY_INVOICE_CP_PARTIALLY_SUCCESS_CN"),
    PAY_INVOICE_COUNTERPARTY_SUCCESS_ACC("PAY_INVOICE","","Partially-ACC", "NOTIF_SCF_PAY_INVOICE_CP_PARTIALLY_SUCCESS_ACC");


    private String type;
    private String status;
    private String typePayment;
    private String templateCode;

    public static TemplateCodeEnum getTemplateCode(String type, String status){
        for(TemplateCodeEnum template : TemplateCodeEnum.values()){
            if ( status == null) {
                status = "";
            }
            if(template.getType().equals(type) && template.getStatus().equals(status)){
                return template;
            }
        }
        return null;
    }

    public static TemplateCodeEnum getTemplateCodePayinvoice(String type, String typePayment){
        for(TemplateCodeEnum template : TemplateCodeEnum.values()){

            if(template.getType().equals(type) && template.getTypePayment().equals(typePayment)){
                return template;
            }
        }
        return null;
    }
}
