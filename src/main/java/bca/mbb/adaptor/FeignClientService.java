package bca.mbb.adaptor;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.RequestClientDto;
import bca.mbb.enums.CoreApiEnum;
import bca.mbb.feign.ExternaCorporatelClient;
import bca.mbb.feign.ExternalEmailClient;
import bca.mbb.feign.UploadManagementClient;
import bca.mbb.mbbcommonlib.exception.GeneralException;
import bca.mbb.mbbcommonlib.response_output.MBBResultEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final ExternalEmailClient externalEmailClients;
    private final ExternaCorporatelClient externaCorporatelClient;
    private final UploadManagementClient uploadManagementClient;

    @Autowired
    private Environment env;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Object callRestApi(CoreApiEnum coreApiEnum, RequestClientDto requestClient) {
        try {
            ExternalClientService externalClient = new ExternalClientService(externalEmailClients, externaCorporatelClient, uploadManagementClient);
            Method method = externalClient.getClass().getDeclaredMethod(env.getProperty(coreApiEnum.getMethodName()), RequestClientDto.class);
            return method.invoke(externalClient, requestClient);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new GeneralException(e);
        }
    }
}
