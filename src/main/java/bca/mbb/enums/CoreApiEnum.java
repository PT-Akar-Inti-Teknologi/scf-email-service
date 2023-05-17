package bca.mbb.enums;

import lombok.Getter;

@Getter
public enum CoreApiEnum {
    SEND_MAIL("class-name.core.api.external", "method-name.management-invoice.sendBodyEmail"),
    EMAIL_USER("class-name.core.api.external", "method-name.email.user"),
    EMAIL_CORPORATE("class-name.core.api.external.corporate", "method-name.email.corporate"),
    GET_GROUP("class-name.core.api.management", "method-name.group");

    private String className;
    private String methodName;

    CoreApiEnum(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
}
