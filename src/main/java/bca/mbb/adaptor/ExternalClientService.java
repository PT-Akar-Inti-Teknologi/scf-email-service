package bca.mbb.adaptor;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.EmailCorporateDto;
import bca.mbb.dto.sendMail.RequestBodySendEmail;
import bca.mbb.dto.sendMail.RequestClientDto;
import bca.mbb.feign.ExternaCorporatelClient;
import bca.mbb.feign.ExternalEmailClient;
import bca.mbb.feign.UploadManagementClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalClientService {

    private final ExternalEmailClient externalClient;

    private final ExternaCorporatelClient externaCorporatelClient;

    private final UploadManagementClient uploadManagementClient;

    public ResponseEntity<ApiResponse> getEmailUser(RequestClientDto<RequestBodySendEmail> requestClientDto) {
        return externalClient.getEmailUser( requestClientDto.getEmailCorporateDto());
    }

    public ResponseEntity<ApiResponse> getEmailCorporate(RequestClientDto<EmailCorporateDto> requestClientDto) {
        return externaCorporatelClient.getEmailCorporate( requestClientDto.getEmailCorporateDto());
    }

    public ApiResponse getGroupId(RequestClientDto<RequestBodySendEmail> requestClientDto) {
        return  uploadManagementClient.getGroupId( requestClientDto.getChannelId(), requestClientDto.getUserId(), requestClientDto.getGroupsDtos());
    }
}
