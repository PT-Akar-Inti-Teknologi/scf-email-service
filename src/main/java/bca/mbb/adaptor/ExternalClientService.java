package bca.mbb.adaptor;

import bca.mbb.dto.sendMail.RequestBodySendEmail;
import bca.mbb.dto.sendMail.RequestClientDto;
import bca.mbb.feign.ManagementInvoiceCoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalClientService {

    private final ManagementInvoiceCoreClient managementInvoiceCoreClient;

    public void sendBodyEmail(RequestClientDto<RequestBodySendEmail> requestClientDto) {
        managementInvoiceCoreClient.sendBodyEmail(requestClientDto.getChannelId(), requestClientDto.getUserId(), requestClientDto.getRequest());
    }
}
