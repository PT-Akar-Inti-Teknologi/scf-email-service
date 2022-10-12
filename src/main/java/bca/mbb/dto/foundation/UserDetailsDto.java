package bca.mbb.dto.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    @JsonProperty("corpId")
    private String corpId;

    @JsonProperty("userId")
    private String userId;
}
