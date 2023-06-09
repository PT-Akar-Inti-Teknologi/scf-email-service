package bca.mbb.service;

import bca.mbb.dto.sendmail.*;
import bca.mbb.enums.email.EmailEnum;
import bca.mbb.enums.email.TemplateCodeEnum;
import bca.mbb.enums.email.TransactionPrefixEnum;
import bca.mbb.service.helper.EmailAccountMapperService;
import bca.mbb.util.Constant;
import bca.mbb.util.ConstantEmail;
import com.bca.eai.email.async.EAIEmailRequest;
import com.bca.eai.email.async.EmailAsyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bca.annotation.EnableLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    ObjectMapper objectMapper;

    private final EmailAsyncService emailAsyncService;

    private final EmailAccountMapperService emailAccountMapperService;

    public void buildEmailGeneric(RequestBodySendBodyEmail bodyEmail, List<GroupsDto> groupsDto) {

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

        List<String>finalPrincipalEmails=new ArrayList<>();
        List<String>finalCounterpartyEmails=new ArrayList<>();

        emailAccountMapperService.getEmailPrincipalAndCounterParty(finalPrincipalEmails,finalCounterpartyEmails,requestClient,bodyEmail);

        bodyEmail.setType(Constant.PRINCIPAL);
        bodyEmail.setChannelId(Constant.MBBSCF);

        mapData(bodyEmail.getPrincipal(), bodyEmail, finalPrincipalEmails.stream().distinct().collect(Collectors.joining(";")));

        if(!CollectionUtils.isEmpty(bodyEmail.getCounterparty()) && bodyEmail.isSuccess()) {
            bodyEmail.setType(Constant.COUNTERPARTY);
            mapData(bodyEmail.getCounterparty(), bodyEmail, finalCounterpartyEmails.stream().distinct().collect(Collectors.joining(";")));
        }
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

            String templateCodes = null;

            if (bodyEmail.getTransactionType().equals("UPLOAD_INVOICE")) {
                 templateCodes = Objects.requireNonNull(TemplateCodeEnum.getTemplateCode(bodyEmail.getTransactionType(), dataMaps.get(0).get("status"))).getTemplateCode();
            } else {
                templateCodes = Objects.requireNonNull(TemplateCodeEnum.getTemplateCodePayinvoice(bodyEmail.getTransactionType(), dataMaps.get(0).get("typePayment"))).getTemplateCode();
            }

            var email= EAIEmailRequest.builder()
                    .channelId(bodyEmail.getChannelId())
                    .emailId(emailIds)
                    .email(emailDestination)
                    .templateCode(templateCodes)
                    .attachments(new ArrayList<>())
                    .parameter(IntStream.range(0, dataMaps.size())
                            .mapToObj(index -> {
                                var lang = index > 0 ? "eng" : "in";

                                List<EmailEnum> emailEnumList = bodyEmail.getType().equals(Constant.PRINCIPAL) ? ConstantEmail.PRINCIPAL_LIST
                                        : ConstantEmail.COUNTERPARTY_LIST;

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
                            ).findFirst().orElse(null);
                    if(enums!=null){
                        Map<String, String> mapTemp = new HashMap<>();
                        mapTemp.put("KEY", language.equals("in") ? enums.getFieldValueInd() : enums.getFieldValueEng());
                        mapTemp.put("VALUE", entry.getValue());
                        return mapTemp;
                    }
                    return null;

                }).filter(Objects::nonNull)
                .forEach(mapTemp -> (dataEmailBody).add(new HashMap<>(mapTemp)));
    }
}
