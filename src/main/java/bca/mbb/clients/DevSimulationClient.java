package bca.mbb.clients;

import bca.mbb.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "devSimulationClient", url="${dev-simulation.url}")
public interface DevSimulationClient {
    @PostMapping(value = "/api/transaction-detail-history/authorized")
    ResponseEntity<ApiResponse> transactionDetailHistoryAuthorize(@RequestHeader(name = "USER-DETAILS") String userDetails,
                                                                  @RequestParam(name = "transaction-stream-id") String transactionStreamId);
}
