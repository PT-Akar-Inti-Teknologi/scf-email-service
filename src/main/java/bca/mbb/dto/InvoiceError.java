package bca.mbb.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceError {
    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_desc_eng")
    private String errorDescEng;

    @JsonProperty("error_desc_ind")
    private String errorDescInd;

    @JsonProperty("line")
    private String line;

}

