package bca.mbb.enums.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateCodeEnum {

    UPLOAD_INVOICE_SUCCES("UPLOAD_INVOICE", "Success", "NOTIF_EMAIL_SCF_SUCCESS_UPLOAD_PRINCIPAL_MBB"),
    UPLOAD_INVOICE_FAILED("UPLOAD_INVOICE","Failed", "NOTIF_EMAIL_SCF_FAILED_UPLOAD_PRINCIPAL_MBB"),
    UPLOAD_INVOICE_COUNTERPARTY("UPLOAD_INVOICE","", "NOTIF_EMAIL_SCF_COUNTERPARTY_UPLOAD_INVOICE_MBB");

    private String type;
    private String status;
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
}
