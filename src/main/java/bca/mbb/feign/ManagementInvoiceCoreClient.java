package bca.mbb.feign;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.RequestBodySendEmail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "managementInvoiceCoreClient", url="${external-client.management-invoice.port}")
public interface ManagementInvoiceCoreClient {
    @PostMapping("/core/scf/email/send-body-email")
    ResponseEntity<ApiResponse> sendBodyEmail(
            @RequestHeader("channel-id") String channelId,
            @RequestHeader("x-actor-id") String username,
            @RequestBody RequestBodySendEmail bodyEmail
    );
}
