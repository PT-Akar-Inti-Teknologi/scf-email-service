package bca.mbb.mapper;

import bca.mbb.dto.sendMail.RequestBodySendBodyEmail;
import bca.mbb.dto.sendMail.RequestBodySendEmail;
import bca.mbb.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lib.fo.dto.email.counterparty.UploadInvoiceCounterParty;
import lib.fo.dto.email.principal.UploadInvoicePrincipal;
import lib.fo.entity.FoInvoiceErrorDetailEntity;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(
        builder = @Builder(disableBuilder = true),
        imports = {Boolean.class, CommonUtil.class, StatusEnum.class, ActionEnum.class}
)
public abstract class RequestBodySendMailMapper {
    public static final RequestBodySendMailMapper INSTANCE = Mappers.getMapper(RequestBodySendMailMapper.class);

    @Mapping(target = "transactionType", source = "foTransactionHeader.transactionName")
    @Mapping(target = "streamTransactionCode", source = "foTransactionHeader.foTransactionHeaderId")
    @Mapping(target = "success", ignore = true)
    @Mapping(target = "single", expression = "java(Boolean.FALSE)")
    @Mapping(target = "principal", ignore = true)
    @Mapping(target = "counterparty", ignore = true)
    @Mapping(target = "corpId",source = "foTransactionHeader.corporateCode")
    public abstract RequestBodySendBodyEmail from(FoTransactionHeaderEntity foTransactionHeader, String currency, ObjectMapper mapper, FoInvoiceErrorDetailEntity errorDetail, Environment env);

    @Mapping(target = "status", expression = "java(CommonUtil.statusTranslate(foTransactionHeader.getStatus(), isEng))")
    @Mapping(target = "reason", expression = "java(!foTransactionHeader.getStatus().equals(StatusEnum.DONE) ? isEng ? errorDetail.getErrorDescriptionEng() : errorDetail.getErrorDescriptionInd() : null)")
    @Mapping(target = "tanggalTransaksi", expression = "java(isEng ? CommonUtil.localDateTimeToEnglish(foTransactionHeader.getCreatedDate()) : CommonUtil.localDateTimeToIndonesia(foTransactionHeader.getCreatedDate()))")
    @Mapping(target = "totalNominal", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalAmount()))")
    @Mapping(target = "totalRecord", source = "foTransactionHeader.totalRecord")
    @Mapping(target = "fileTransaksi", source = "foTransactionHeader.fileName")
    @Mapping(target = "tipeUpload", expression = "java(CommonUtil.typeTranslate(ActionEnum.valueOf(foTransactionHeader.getTransactionType()), isEng))")
    @Mapping(target = "keterangan", source = "foTransactionHeader.remarks")
    @Mapping(target = "noReferensi", source = "foTransactionHeader.referenceNumber")
    @Mapping(target = "currency", source = "currency")
    public abstract UploadInvoicePrincipal fromInvoicePrincipal(FoTransactionHeaderEntity foTransactionHeader, boolean isEng, String currency, FoInvoiceErrorDetailEntity errorDetail, Environment env);

    @Mapping(target = "tanggalTransaksi", expression = "java(isEng ? CommonUtil.localDateTimeToEnglish(foTransactionHeader.getCreatedDate()) : CommonUtil.localDateTimeToIndonesia(foTransactionHeader.getCreatedDate()))")
    @Mapping(target = "totalNominal", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalAmount()))")
    @Mapping(target = "totalRecord", source = "foTransactionHeader.totalRecord")
    @Mapping(target = "tipeUpload", expression = "java(CommonUtil.typeTranslate(ActionEnum.valueOf(foTransactionHeader.getTransactionType()), isEng))")
    @Mapping(target = "keterangan", source = "foTransactionHeader.remarks")
    @Mapping(target = "noReferensi", source = "foTransactionHeader.referenceNumber")
    @Mapping(target = "corpName", source = "foTransactionHeader.corporateCode")
    public abstract UploadInvoiceCounterParty fromInvoiceCounterparty(FoTransactionHeaderEntity foTransactionHeader, boolean isEng, String currency);

    @AfterMapping
    protected void getSendEmailDto(@MappingTarget RequestBodySendBodyEmail requestBodySendEmail, FoTransactionHeaderEntity foTransactionHeader, String currency, ObjectMapper mapper, FoInvoiceErrorDetailEntity errorDetail, Environment env) {
        requestBodySendEmail.setSuccess(foTransactionHeader.getStatus().equals(StatusEnum.DONE) ? Boolean.TRUE : Boolean.FALSE);

        List<Map<String, String>> principal = new ArrayList<>();
        principal.add(mapper.convertValue(fromInvoicePrincipal(foTransactionHeader, Boolean.TRUE, currency, errorDetail, env), Map.class));
        principal.add(mapper.convertValue(fromInvoicePrincipal(foTransactionHeader, Boolean.FALSE, currency, errorDetail, env), Map.class));
        requestBodySendEmail.setPrincipal(principal);

        List<Map<String, String>> counterparty = new ArrayList<>();
        if (requestBodySendEmail.isSuccess()) {
            counterparty.add(mapper.convertValue(fromInvoiceCounterparty(foTransactionHeader, Boolean.TRUE, currency), Map.class));
            counterparty.add(mapper.convertValue(fromInvoiceCounterparty(foTransactionHeader, Boolean.FALSE, currency), Map.class));
            requestBodySendEmail.setCounterparty(counterparty);
        }
    }
}
