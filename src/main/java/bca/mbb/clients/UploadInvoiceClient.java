package bca.mbb.clients;

import bca.mbb.dto.ApiResponse;
import bca.mbb.mbbcommonlib.response_output.MBBResultEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "uploadInvoiceClient", url="${server.upload-invoice.url}")
public interface UploadInvoiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "/core/scf/invoice/upload/validate")
    ResponseEntity<MBBResultEntity> saveUpload(@RequestHeader("x-actor-id") String actor,
                                                                 @RequestHeader("channel-id") String channel,
                                                                 @RequestHeader("x-access-path") String path,
                                                                 @RequestHeader("USE-TOKEN") String userToken,
                                                                 @RequestParam("ws-id") String wsId,
                                                                 @RequestParam("chaining-id") String chainingId,
                                                                 @RequestParam("upload_request_data") String data);

    @RequestMapping(method = RequestMethod.GET, value = "/core/scf/invoice/upload/list-principal")
    ResponseEntity<MBBResultEntity> listPrincipal(@RequestHeader("channel-id") String channel,
                                                  @RequestHeader("x-access-path") String path,
                                                  @RequestHeader("x-actor-id") String actor,
                                                  @RequestParam("principal-code") String principalCode,
                                                   @RequestParam("principal-name") String principalName,
                                                   @RequestParam("page") Integer page,
                                                  @RequestParam("page-size") Integer pageSize);

    @PostMapping(value = "/core/scf/invoice/upload/validated-invoice")
    ResponseEntity<ApiResponse> uploadValidatedInvoice(@RequestHeader("x-actor-id") String actor,
                                                       @RequestHeader("channel-id") String channel,
                                                       @RequestBody LinkedMultiValueMap<String, Object> request);
}
