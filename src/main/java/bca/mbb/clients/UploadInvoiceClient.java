package bca.mbb.clients;

import bca.mbb.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "uploadInvoiceClient", url="${server.upload-invoice.url}")
public interface UploadInvoiceClient {
    @PostMapping(value = "/core/scf/invoice/upload/validated-invoice")
    ResponseEntity<ApiResponse> uploadValidatedInvoice(@RequestHeader("x-actor-id") String actor,
                                                       @RequestHeader("channel-id") String channel,
                                                       @RequestBody LinkedMultiValueMap<String, Object> request);
}
