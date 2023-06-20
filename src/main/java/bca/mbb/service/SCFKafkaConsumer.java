package bca.mbb.service;

import bca.mbb.dto.InvoiceError;
import bca.mbb.dto.sendMail.GroupsDto;
import bca.mbb.mapper.FoInvoiceErrorDetailEntityMapper;
import bca.mbb.mapper.RequestBodySendMailMapper;
import bca.mbb.repository.FoInvoiceErrorDetailRepository;
import bca.mbb.repository.FoTransactionDetailRepository;
import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.scf.avro.EmailScfData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lib.fo.entity.FoInvoiceErrorDetailEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
@Slf4j
public class SCFKafkaConsumer {

    @Value("${channel-id}")
    private String channelId;

    private final FoInvoiceErrorDetailRepository foInvoiceErrorDetailRepository;
    private final FoTransactionHeaderRepository foTransactionHeaderRepository;
    private final FoTransactionDetailRepository foTransactionDetailRepository;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final EmailService emailService;

    @Autowired
    private Environment env;

    @KafkaListener(topics = "#{'${app.kafka.topic.email}'}", groupId = "#{'${spring.kafka.consumer.group-id-email}'}", containerFactory = "emailListener")
    public void validateDoneListen(EmailScfData message) {

        var header = foTransactionHeaderRepository.findByFoTransactionHeaderId(message.getTransactionId());

        var headerDto = foTransactionHeaderRepository.getCounterparty(header.getTransactionHeaderId());

        var foTransactionDetail = foTransactionDetailRepository.groupByProgramCodeSellerCodeBuyerCode(message.getTransactionId());

        Set<String> errMsgEn = new LinkedHashSet<>();

        Set<String> errMsgId = new LinkedHashSet<>();

        var currency = foTransactionDetailRepository.getCurrencyByFoTransactionId(header.getFoTransactionHeaderId());

        if (header.getStatus().equals(StatusEnum.FAILED) && header.getTransactionName().equals("UPLOAD_INVOICE")) {

            var foInvoiceErrorDetail =foInvoiceErrorDetailRepository.findByChainingId(header.getChainingId());

            foInvoiceErrorDetail.stream().forEach(error-> {
                errMsgId.add(error.getErrorDescriptionInd());
                errMsgEn.add(error.getErrorDescriptionEng());
            });
        }

        var listGroup = new ArrayList<GroupsDto>();

        foTransactionDetail.forEach(data-> {
            var singleGroup = new GroupsDto();

            singleGroup.setProgramCode(data.getProgramCode());
            singleGroup.setBuyerCode(data.getBuyerId());
            singleGroup.setSellerCode(data.getSellerId());

            listGroup.add(singleGroup);
        });

        var errorDetail = FoInvoiceErrorDetailEntity.builder().errorDescriptionEng(String.join(",", errMsgEn)).errorDescriptionInd(String.join(",", errMsgId)).build();

        var requestBodySendEmail = RequestBodySendMailMapper.INSTANCE.from(header, currency, mapper, errorDetail, env, headerDto);

        emailService.buildEmailGeneric(requestBodySendEmail, listGroup);

    }
}
