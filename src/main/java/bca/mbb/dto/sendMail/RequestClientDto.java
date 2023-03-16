package bca.mbb.dto.sendMail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestClientDto<T> {
    private List<T> requestList;
    private T request;
    private String path;
    private String userId;
    private String channelId;
    private String principalCode;
    private String principalName;
    private String userDetailDto;
    private String clientId;
    private Map<String, String> userDetailMap;
    private String transactionStreamId;
    private int page;
    private int size;
}
