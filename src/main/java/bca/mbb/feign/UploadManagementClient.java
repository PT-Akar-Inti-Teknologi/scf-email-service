package bca.mbb.feign;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendmail.GroupsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "uploadManagementClient", url="${external-client.upload.port}")
public interface UploadManagementClient {

    @PostMapping("/core/scf/invoice/upload/group-id")
    ApiResponse getGroupId(
            @RequestHeader("channel-id") String channelId,
            @RequestHeader("x-actor-id") String username,
            @RequestBody List<GroupsDto> group
    );
}
