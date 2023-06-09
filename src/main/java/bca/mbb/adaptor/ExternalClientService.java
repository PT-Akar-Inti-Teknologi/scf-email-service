package bca.mbb.adaptor;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendmail.EmailCorporateDto;
import bca.mbb.dto.sendmail.RequestBodySendEmail;
import bca.mbb.dto.sendmail.RequestClientDto;
import bca.mbb.feign.ExternalCorporatelClient;
import bca.mbb.feign.ExternalEmailClient;
import bca.mbb.feign.UploadManagementClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalClientService {

    private final ExternalEmailClient externalClient;

    private final ExternalCorporatelClient externaCorporatelClient;

    private final UploadManagementClient uploadManagementClient;

    public ResponseEntity<ApiResponse> getEmailUser(RequestClientDto<RequestBodySendEmail> requestClientDto) {
        return externalClient.getEmailUser( requestClientDto.getEmailCorporateDto());
    }

    public ResponseEntity<ApiResponse> getEmailCorporate(RequestClientDto<EmailCorporateDto> requestClientDto) {
        return externaCorporatelClient.getEmailCorporate( requestClientDto.getEmailCorporateDto(), requestClientDto.getEmailCorporateDto().isSingle(),
                requestClientDto.getEmailCorporateDto().getStreamTransactionCode());
    }

    public ApiResponse getGroupId(RequestClientDto<RequestBodySendEmail> requestClientDto) {
        return  uploadManagementClient.getGroupId( requestClientDto.getChannelId(), requestClientDto.getUserId(), requestClientDto.getGroupsDtos());
    }
}
