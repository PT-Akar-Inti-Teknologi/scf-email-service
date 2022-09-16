package bca.mbb.clients;

import bca.mbb.dto.ApiResponse;
import bca.mbb.mbbcommonlib.response_output.MBBResultEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "uploadInvoiceClient", url="${server.upload-invoice.url}")
public interface UploadInvoiceClient {
    @PostMapping(value = "/core/scf/invoice/upload/validated-invoice")
    ResponseEntity<ApiResponse> uploadValidatedInvoice(@RequestHeader("x-actor-id") String actor,
                                                       @RequestHeader("channel-id") String channel,
                                                       @RequestBody LinkedMultiValueMap<String, Object> request);
}
