package bca.mbb.adaptor;

import bca.mbb.dto.sendmail.RequestClientDto;
import bca.mbb.enums.CoreApiEnum;
import bca.mbb.feign.ExternalCorporatelClient;
import bca.mbb.feign.ExternalEmailClient;
import bca.mbb.feign.UploadManagementClient;
import bca.mbb.mbbcommonlib.exception.GeneralException;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
public class FeignClientService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalEmailClient externalEmailClients;
    private final ExternalCorporatelClient externalCorporatelClient;
    private final UploadManagementClient uploadManagementClient;

    @Autowired
    private Environment env;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Object callRestApi(CoreApiEnum coreApiEnum, RequestClientDto requestClient) {
        try {
            ExternalClientService externalClient = new ExternalClientService(externalEmailClients, externalCorporatelClient, uploadManagementClient);
            Method method = externalClient.getClass().getDeclaredMethod(coreApiEnum.getMethodName(), RequestClientDto.class);
            return method.invoke(externalClient, requestClient);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.info("exception when call:{} at time:{}",env.getProperty(coreApiEnum.getMethodName()), LocalDateTime.now());
            throw new GeneralException(e);
        }
    }
}
