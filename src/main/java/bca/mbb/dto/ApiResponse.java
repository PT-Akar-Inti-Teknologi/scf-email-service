package bca.mbb.dto;

import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse {
    @JsonProperty("error_schema")
    private ErrorSchema errorSchema;

    @JsonProperty("output_schema")
    private Object outputSchema;

    public ApiResponse(){}

    public ApiResponse(Object outputSchema) {
        this.errorSchema = new ErrorSchema(CommonUtil.successCode, new ErrorMessage("Success", "Berhasil"));
        this.outputSchema = outputSchema;
    }

    @JsonIgnore
    public String getErrorCode(){
        return errorSchema.getErrorCode();
    }

    @JsonIgnore
    public String getErrorMessageEn(){
        return errorSchema.getErrorMessage().english;
    }

    @JsonIgnore
    public String getErrorMessageInd(){
        return errorSchema.getErrorMessage().indonesian;
    }

    public ApiResponse(String errorCode, String english, String indonesia, Object data) {
        this.errorSchema = new ErrorSchema(errorCode, new ErrorMessage(english, indonesia));
        this.outputSchema = data;
    }

    @Data
    private static final class ErrorSchema {
        @JsonProperty("error_code")
        private String errorCode;

        @JsonProperty("error_message")
        private ErrorMessage errorMessage;

        @SuppressWarnings("java:S1116")
        public ErrorSchema(){}

        public ErrorSchema(String errorCode, ErrorMessage errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    @Data
    private static final class ErrorMessage {
        private String english;
        private String indonesian;

        @SuppressWarnings("java:S1116")
        public ErrorMessage(){}

        public ErrorMessage(String english, String indonesian) {
            this.english = english;
            this.indonesian = indonesian;
        }
    }
}
