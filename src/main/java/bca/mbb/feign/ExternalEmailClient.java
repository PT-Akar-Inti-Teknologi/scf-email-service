package bca.mbb.feign;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.EmailCorporateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "externalClient", url="${external-client.email}")
public interface ExternalEmailClient {

    @PostMapping("/api/notification/email-address")
    ResponseEntity<ApiResponse> getEmailUser(
            @RequestBody EmailCorporateDto bodyEmail
    );
}
