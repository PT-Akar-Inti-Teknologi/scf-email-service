package bca.mbb.enums;

import lombok.Getter;

@Getter
public enum CoreApiEnum {
    EMAIL_USER("ExternalClientService", "getEmailUser"),
    EMAIL_CORPORATE("ExternalCorporateService", "getEmailCorporate"),
    GET_GROUP("InvoiceManagementClientService", "getGroupId");

    private String className;
    private String methodName;

    CoreApiEnum(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
}
