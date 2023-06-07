package bca.mbb.feign;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.EmailCorporateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "externalCorporateClient", url="${external-client.corporate}")
public interface ExternalCorporatelClient {

    @PostMapping("/api/corporate/email-scf")
    ResponseEntity<ApiResponse> getEmailCorporate(
            @RequestBody EmailCorporateDto bodyEmail,
            @RequestHeader("single") boolean single,
            @RequestHeader("stream_transaction_code") String streamTransactionCode);
}
