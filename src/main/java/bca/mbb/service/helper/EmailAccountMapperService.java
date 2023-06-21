package bca.mbb.service.helper;

import bca.mbb.adaptor.FeignClientService;
import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendmail.EmailCorporateDto;
import bca.mbb.dto.sendmail.EmailCorporateDto.ObjectDto;
import bca.mbb.dto.sendmail.RequestBodySendBodyEmail;
import bca.mbb.dto.sendmail.RequestClientDto;
import bca.mbb.dto.sendmail.ResponseEmailHeaderDto;
import bca.mbb.enums.CoreApiEnum;
import bca.mbb.util.Constant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAccountMapperService {
    @Autowired
    ObjectMapper objectMapper;

    private final FeignClientService feignClientService;

    public void getEmailPrincipalAndCounterParty(List<String> finalPrincipalEmails,List<String> finalCounterpartyEmails,
            RequestClientDto<Object> requestClient, RequestBodySendBodyEmail bodyEmail){
        getGroupData(bodyEmail,requestClient);
        var externalCorporate = (ResponseEntity) feignClientService.callRestApi(CoreApiEnum.EMAIL_CORPORATE, requestClient);

        var responseCorporate = objectMapper.convertValue(externalCorporate.getBody(), ApiResponse.class);
        Map<String,String> mapEmails=new HashMap<>();
        List<String>principalEmails=new ArrayList<>();
        List<String>counterpartyEmails=new ArrayList<>();

        if (!responseCorporate.getErrorCode().equalsIgnoreCase("MBB-00-000"))
            return;

        var outputSchemaCorporates = objectMapper.convertValue(responseCorporate.getOutputSchema(), EmailCorporateDto.class);
        outputSchemaCorporates.getCorporate().stream()
//                    .filter(corporateDto -> corporateDto.getCorporateCorpId().equalsIgnoreCase(bodyEmail.getCorpId()))
                .forEach(corporateDto -> {
                    var emailWithoutComma=corporateDto.getEmail().replace(";","");
                    mapEmails.put(emailWithoutComma, corporateDto.getPartyType());
                    if (corporateDto.getPartyType().equals(Constant.PRINCIPAL)) {
                        principalEmails.addAll(Arrays.asList(corporateDto.getEmail().split(";")));
                    } else {
                        counterpartyEmails.addAll(Arrays.asList(corporateDto.getEmail().split(";")));
                    }
                });

        var externalEmailUser = (ResponseEntity) feignClientService.callRestApi(CoreApiEnum.EMAIL_USER, requestClient);

        var responseEmailUser = objectMapper.convertValue(externalEmailUser.getBody(), ApiResponse.class);
        if (!responseEmailUser.getErrorCode().equalsIgnoreCase("MBB-00-000")){
            return;
        }
        var outputSchemaUser = objectMapper.convertValue(responseEmailUser.getOutputSchema(), ResponseEmailHeaderDto.class);
        var emailUser=Arrays.asList(outputSchemaUser.getEmailAddress().split(";"));

        emailUser.stream()
                .filter(userChecks -> mapEmails.get(userChecks) != null)
                .findFirst()
                .ifPresent(userChecks -> {
                    if (mapEmails.get(userChecks).equals(Constant.PRINCIPAL)) {
                        finalPrincipalEmails.addAll(emailUser);
                    } else {
                        finalCounterpartyEmails.addAll(emailUser);
                    }
                });

    }

    private void getGroupData(RequestBodySendBodyEmail bodyEmail,RequestClientDto<Object> requestClient){
        var externalGroup = feignClientService.callRestApi(CoreApiEnum.GET_GROUP, requestClient);

        var responseGroup = objectMapper.convertValue(externalGroup, ApiResponse.class);

        if (!responseGroup.getErrorCode().equalsIgnoreCase("SCF-00-000"))
            return;
        var outputSchemaGroup = objectMapper.convertValue(responseGroup.getOutputSchema(), new TypeReference<List<ObjectDto>>() {});
        var requestCorporate = new EmailCorporateDto();

        requestCorporate.setSingle(bodyEmail.isSingle());
        requestCorporate.setStreamTransactionCode(bodyEmail.getStreamTransactionCode());

        var listObject = new ArrayList<ObjectDto>();
        outputSchemaGroup.forEach( objectDto -> {

            listObject.add(EmailCorporateDto.ObjectDto.builder()
                    .counterpartyCode(null)
                    .programParameterGroupPrincipalId(objectDto.getProgramParameterGroupPrincipalId())
                    .build());

            listObject.add(EmailCorporateDto.ObjectDto.builder()
                    .counterpartyCode(objectDto.getCounterpartyCode())
                    .programParameterGroupPrincipalId(objectDto.getProgramParameterGroupPrincipalId())
                    .build());
        });
        requestCorporate.setObject(listObject);

        requestClient.setEmailCorporateDto(requestCorporate);

    }
}
