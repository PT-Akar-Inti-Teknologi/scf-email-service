package bca.mbb.enums;

import lombok.Getter;

@Getter
public enum CoreApiEnum {
    SEND_MAIL("class-name.core.api.management", "method-name.management-invoice.sendBodyEmail");

    private String className;
    private String methodName;

    CoreApiEnum(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
}
