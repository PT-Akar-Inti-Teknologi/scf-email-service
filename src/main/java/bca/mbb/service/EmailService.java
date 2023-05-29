package bca.mbb.service;

import bca.mbb.adaptor.FeignClientService;
import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.sendMail.*;
import bca.mbb.enums.CoreApiEnum;
import bca.mbb.enums.email.EmailEnum;
import bca.mbb.enums.email.TemplateCodeEnum;
import bca.mbb.enums.email.TransactionPrefixEnum;
import bca.mbb.mbbcommonlib.response_output.ErrorSchema;
import bca.mbb.mbbcommonlib.response_output.MBBResultEntity;
import bca.mbb.util.Constant;
import bca.mbb.util.ConstantEmail;
import com.bca.eai.email.async.EAIEmailRequest;
import com.bca.eai.email.async.EmailAsyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bca.annotation.EnableLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@EnableLogging
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    @Value("${channel-email}") private String channelId;

    private static final String MBB_SUCCESS_CODE = "SCF-00-000";
    private static final String MBB_SUCCESS_EN = "success";
    private static final String MBB_SUCCESS_IND = "sukses";

    @Autowired
    ObjectMapper objectMapper;

    private final EmailAsyncService emailAsyncService;

    private final FeignClientService feignClientService;

    public ResponseEntity<MBBResultEntity<Object>> buildEmailGeneric(RequestBodySendBodyEmail bodyEmail, List<GroupsDto> groupsDto) {

        var emailBcc = RequestBodySendBodyEmail.builder()
                .streamTransactionCode(bodyEmail.getStreamTransactionCode())
                .single(bodyEmail.isSingle())
                .build();

        var requestClient = RequestClientDto.builder()
                .channelId(channelId)
                .userId(Constant.MBBSCF)
                .requestBodySendBodyEmail(emailBcc)
                .groupsDtos(groupsDto)
                .build();

        var externalGroup = feignClientService.callRestApi(CoreApiEnum.GET_GROUP, requestClient);

        var responseGroup = objectMapper.convertValue(externalGroup, ApiResponse.class);

        if (responseGroup.getErrorCode().equalsIgnoreCase("SCF-00-000")) {

            var outputSchemaGroup = objectMapper.convertValue(responseGroup.getOutputSchema(), new TypeReference<List<EmailCorporateDto.ObjectDto>>() {});
            var requestCorporate = new EmailCorporateDto();

            requestCorporate.setSingle(bodyEmail.isSingle());
            requestCorporate.setStreamTransactionCode(bodyEmail.getStreamTransactionCode());

            var listObject = new ArrayList<EmailCorporateDto.ObjectDto>();
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

        var externalCorporate = (ResponseEntity) feignClientService.callRestApi(CoreApiEnum.EMAIL_CORPORATE, requestClient);

        var responseCorporate = objectMapper.convertValue(externalCorporate.getBody(), ApiResponse.class);
        Map<String,String>mapEmails=new HashMap<>();
        List<String>principalEmails=new ArrayList<>();
        List<String>counterpartyEmails=new ArrayList<>();
        if (responseCorporate.getErrorCode().equalsIgnoreCase("MBB-00-000")) {

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
        }

        var externalEmailUser = (ResponseEntity) feignClientService.callRestApi(CoreApiEnum.EMAIL_USER, requestClient);

        var responseEmailUser = objectMapper.convertValue(externalEmailUser.getBody(), ApiResponse.class);
        List<String> finalPrincipalEmails = principalEmails;
        List<String> finalCounterpartyEmails = counterpartyEmails;
        if (responseEmailUser.getErrorCode().equalsIgnoreCase("MBB-00-000")) {

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


        bodyEmail.setType(Constant.PRINCIPAL);
        bodyEmail.setChannelId(Constant.MBBSCF);
        mapData(bodyEmail.getPrincipal(), bodyEmail, finalPrincipalEmails.stream().distinct().collect(Collectors.joining(";")));

        if(!CollectionUtils.isEmpty(bodyEmail.getCounterparty()) && bodyEmail.isSuccess()) {
            bodyEmail.setType(Constant.COUNTERPARTY);
            mapData(bodyEmail.getCounterparty(), bodyEmail, finalCounterpartyEmails.stream().distinct().collect(Collectors.joining(";")));
        }

        return new ResponseEntity<>(new MBBResultEntity<>(true, new ErrorSchema(MBB_SUCCESS_CODE, new ErrorSchema.ErrorMessage(MBB_SUCCESS_EN, MBB_SUCCESS_IND))), HttpStatus.OK);
    }

    private void mapData(List<Map<String,String>> dataMaps, RequestBodySendBodyEmail bodyEmail,  String emailBcc){
        try {
            var timestamp = new Timestamp(System.currentTimeMillis());

            var milliseconds = timestamp.getTime();

            var emailIds = Constant.EMAILID + TransactionPrefixEnum.getPrefix(bodyEmail.getTransactionType()) + "-" + milliseconds;

            var emailDestination = EAIEmailRequest.Email.builder()
                    .emailTo("")
                    .emailCc("")
                    .emailBcc(emailBcc)
                    .emailCustomSubject(bodyEmail.getType().equals(Constant.PRINCIPAL) ? Constant.SUBJECT_PRINCIPAL : Constant.SUBJECT_COUNTERPARTY)
                    .build();

            var templateCodes = Objects.requireNonNull(TemplateCodeEnum.getTemplateCode(bodyEmail.getTransactionType(), dataMaps.get(0).get("status"))).getTemplateCode();

            var email= EAIEmailRequest.builder()
                    .channelId(bodyEmail.getChannelId())
                    .emailId(emailIds)
                    .email(emailDestination)
                    .templateCode(templateCodes)
                    .attachments(new ArrayList<>())
                    .parameter(IntStream.range(0, dataMaps.size())
                            .mapToObj(index -> {
                                var lang = index > 0 ? "eng" : "in";

                                List<EmailEnum> emailEnumList = bodyEmail.getType().equals(Constant.PRINCIPAL) ? ConstantEmail.UPLOAD_INVOICE_PRINCIPAL_LIST
                                        : ConstantEmail.UPLOAD_INVOICE_COUNTER_PARTY_LIST;

                                List<HashMap<String, String>> dataEmailBody = new ArrayList<>();

                                mapDataDetailParameter(dataMaps.get(index), lang, dataEmailBody, emailEnumList);

                                return dataEmailBody;
                            }).flatMap(Collection::stream).collect(Collectors.toList()))
                    .build();

            sendEmail(email);

        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    private void sendEmail(EAIEmailRequest emailRequestDto) {
        try {
            log.info("Email request DTO:{} time :{}",emailRequestDto, LocalDateTime.now());

            emailAsyncService.sendEmail(emailRequestDto);

            log.info("ALREADY SEND");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapDataDetailParameter(Map<String, String> maps,
                                        String language, List<HashMap<String, String>> dataEmailBody, List<EmailEnum> emailEnumList) {
        maps.entrySet().stream()
                .map(entry -> {
                    var enums = emailEnumList.stream()
                            .filter(emailEnum ->
                                    emailEnum.getFieldName().equals(entry.getKey())
                            ).findFirst().get();
                    Map<String, String> mapTemp = new HashMap<>();
                    mapTemp.put("KEY", language.equals("in") ? enums.getFieldValueInd() : enums.getFieldValueEng());
                    mapTemp.put("VALUE", entry.getValue());
                    return mapTemp;
                })
                .forEach(mapTemp -> {
                    (dataEmailBody).add(new HashMap<>(mapTemp));
                });
    }
}
