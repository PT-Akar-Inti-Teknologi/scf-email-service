package bca.mbb.feign;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.GroupsDto;
import bca.mbb.dto.sendMail.RequestBodySendBodyEmail;
import bca.mbb.dto.sendMail.RequestBodySendEmail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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
