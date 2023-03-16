package bca.mbb.adaptor;

import bca.mbb.dto.sendMail.RequestClientDto;
import bca.mbb.enums.CoreApiEnum;
import bca.mbb.feign.ManagementInvoiceCoreClient;
import bca.mbb.mbbcommonlib.response_output.MBBResultEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final ManagementInvoiceCoreClient managementInvoiceCoreClient;

    @Autowired
    private Environment env;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<MBBResultEntity> callRestApi(CoreApiEnum coreApiEnum, RequestClientDto requestClient) {
        var response = new MBBResultEntity<>();
        try {
            ExternalClientService externalClient = new ExternalClientService(managementInvoiceCoreClient);
            Method method = externalClient.getClass().getDeclaredMethod(env.getProperty(coreApiEnum.getMethodName()), RequestClientDto.class);
            method.invoke(externalClient, requestClient);
        } catch (Exception e) {

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
