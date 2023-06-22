package bca.mbb.mapper;

import bca.mbb.dto.FoTransactionHeaderDto;
import bca.mbb.dto.sendmail.RequestBodySendBodyEmail;
import bca.mbb.util.CommonUtil;
import bca.mbb.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lib.fo.dto.email.counterparty.EmailCounterpartyDto;
import lib.fo.dto.email.principal.EmailPrincipalDto;
import lib.fo.entity.FoInvoiceErrorDetailEntity;
import lib.fo.entity.FoTransactionHeaderEntity;
import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
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
    @Mapping(target = "streamTransactionCode", expression = "java(this.setStreamTransactionCode(foTransactionHeader))")
    @Mapping(target = "success", ignore = true)
    @Mapping(target = "single", expression = "java(Boolean.FALSE)")
    @Mapping(target = "principal", ignore = true)
    @Mapping(target = "counterparty", ignore = true)
    @Mapping(target = "corpId",source = "foTransactionHeader.corporateCode")
    public abstract RequestBodySendBodyEmail from(FoTransactionHeaderEntity foTransactionHeader, String currency, ObjectMapper mapper, FoInvoiceErrorDetailEntity errorDetail, Environment env, FoTransactionHeaderDto headerDto);

    @Mapping(target = "status", expression = "java(CommonUtil.statusTranslate(foTransactionHeader.getStatus(), isEng))")
//    @Mapping(target = "reason", expression = "java(!foTransactionHeader.getStatus().equals(StatusEnum.SUCCESS) ? isEng ? errorDetail.getErrorDescriptionEng() : errorDetail.getErrorDescriptionInd() : null)")
    @Mapping(target = "reason",expression = "java(this.setReason(foTransactionHeader,isEng,errorDetail))")
    @Mapping(target = "tanggalTransaksi", expression = "java(isEng ? CommonUtil.localDateTimeToIndonesia(foTransactionHeader.getCreatedDate()) : CommonUtil.localDateTimeToEnglish(foTransactionHeader.getCreatedDate()))")
    @Mapping(target = "totalNominal", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalAmount()))")
    @Mapping(target = "totalRecord", source = "foTransactionHeader.totalRecord")
    @Mapping(target = "fileTransaksi", source = "foTransactionHeader.fileName")
    @Mapping(target = "tipeUpload", expression = "java(CommonUtil.typeTranslate(ActionEnum.valueOf(foTransactionHeader.getTransactionType()), isEng))")
    @Mapping(target = "keterangan", source = "foTransactionHeader.remarks")
    @Mapping(target = "noReferensi", source = "foTransactionHeader.referenceNumber")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "programCode", source = "foTransactionHeader.programCode")
    @Mapping(target = "programName", source = "foTransactionHeader.programName")
    @Mapping(target = "dealerName", source = "headerDto.counterpartyName")
    @Mapping(target = "typePayment", constant = "-")
    public abstract EmailPrincipalDto fromInvoicePrincipal(FoTransactionHeaderEntity foTransactionHeader, boolean isEng, String currency, FoInvoiceErrorDetailEntity errorDetail, Environment env, FoTransactionHeaderDto headerDto);

    @Mapping(target = "tanggalTransaksi", expression = "java(isEng ? CommonUtil.localDateTimeToIndonesia(foTransactionHeader.getCreatedDate()) : CommonUtil.localDateTimeToEnglish(foTransactionHeader.getCreatedDate()))")
    @Mapping(target = "totalNominal", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalAmount()))")
    @Mapping(target = "totalRecord", source = "foTransactionHeader.totalRecord")
    @Mapping(target = "tipeUpload", expression = "java(CommonUtil.typeTranslate(ActionEnum.valueOf(foTransactionHeader.getTransactionType()), isEng))")
    @Mapping(target = "keterangan", source = "foTransactionHeader.remarks")
    @Mapping(target = "noReferensi", source = "foTransactionHeader.referenceNumber")
    @Mapping(target = "corpName", source = "foTransactionHeader.corporateCode")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "programCode", source = "foTransactionHeader.programCode")
    @Mapping(target = "programName", source = "foTransactionHeader.programName")
    @Mapping(target = "dealerName", source = "headerDto.counterpartyName")
    @Mapping(target = "dealerCode", source = "headerDto.counterpartyCode")
    @Mapping(target = "accountAmount", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalAccountTransferAmount()))")
    @Mapping(target = "accountNumber", source = "foTransactionHeader.accountNumber")
    @Mapping(target = "creditNotedAmount", expression = "java(CommonUtil.nominal(foTransactionHeader.getTotalCreditNoteAmount()))")
    @Mapping(target = "creditNoted", expression = "java(this.setCreditNote(foTransactionHeader))")
    @Mapping(target = "totalSucceedAmount", expression = "java(this.setTotalSucceedAmount(headerDto))")
    @Mapping(target = "totalSucceedRecord", expression = "java(this.setTotalSucceedRecord(headerDto))")
    @Mapping(target = "totalFailedAmount", expression = "java(this.setTotalFailedAmount(headerDto))")
    @Mapping(target = "totalFailedRecord", expression = "java(this.setTotalFailedRecord(headerDto))")
    @Mapping(target = "typePayment",       expression = "java(this.setTypePayment(foTransactionHeader))")
    public abstract EmailCounterpartyDto fromInvoiceCounterparty(FoTransactionHeaderEntity foTransactionHeader, boolean isEng, String currency, FoTransactionHeaderDto headerDto);

    @AfterMapping
    protected void getSendEmailDto(@MappingTarget RequestBodySendBodyEmail requestBodySendEmail, FoTransactionHeaderEntity foTransactionHeader, String currency, ObjectMapper mapper, FoInvoiceErrorDetailEntity errorDetail, Environment env, FoTransactionHeaderDto headerDto) {
        requestBodySendEmail.setSuccess(foTransactionHeader.getStatus().equals(StatusEnum.SUCCESS) ? Boolean.TRUE : Boolean.FALSE);

        List<Map<String, String>> principal = new ArrayList<>();
        principal.add(mapper.convertValue(fromInvoicePrincipal(foTransactionHeader, Boolean.TRUE, currency, errorDetail, env, headerDto), Map.class));
        principal.add(mapper.convertValue(fromInvoicePrincipal(foTransactionHeader, Boolean.FALSE, currency, errorDetail, env, headerDto), Map.class));
        requestBodySendEmail.setPrincipal(principal);

        List<Map<String, String>> counterparty = new ArrayList<>();
//        if (requestBodySendEmail.isSuccess()) {
            counterparty.add(mapper.convertValue(fromInvoiceCounterparty(foTransactionHeader, Boolean.TRUE, currency, headerDto), Map.class));
            counterparty.add(mapper.convertValue(fromInvoiceCounterparty(foTransactionHeader, Boolean.FALSE, currency, headerDto), Map.class));
            requestBodySendEmail.setCounterparty(counterparty);
//        }
    }
    protected String setReason(FoTransactionHeaderEntity foTransactionHeader, boolean isEng, FoInvoiceErrorDetailEntity errorDetail){
        String reason=null;

        if (foTransactionHeader.getTransactionType().equals("UPLOAD_INVOICE")) {

            if(foTransactionHeader.getStatus().equals(StatusEnum.SUCCESS) && isEng)
                reason= errorDetail.getErrorDescriptionEng();
            else
                reason= errorDetail.getErrorDescriptionInd();
        } else {
            reason = foTransactionHeader.getReason();
        }

        return reason;
    }

    protected String setCreditNote(FoTransactionHeaderEntity foTransactionHeaderEntity) {
        if (!CommonUtil.isNullOrEmpty(foTransactionHeaderEntity.getCreditNotes())) {
            return foTransactionHeaderEntity.getCreditNotes().replace(",", "<br/>");
        }
        return foTransactionHeaderEntity.getCreditNotes();
    }

    protected boolean checkFoTransactionHeaderDto(FoTransactionHeaderDto headerDto) {
        return CommonUtil.isObjectEmpty(headerDto);
    }

    protected String setTotalSucceedAmount(FoTransactionHeaderDto headerDto) {
        var result = checkFoTransactionHeaderDto(headerDto);

        if (!result) {
            return headerDto.getTotalPaymentSuccess().toString();
        }
        return null;
    }

    protected String setTotalSucceedRecord(FoTransactionHeaderDto headerDto) {
        var result = checkFoTransactionHeaderDto(headerDto);

        if (!result) {
            return headerDto.getTotalInvoiceSuccess().toString();
        }
        return null;
    }

    protected String setTotalFailedAmount(FoTransactionHeaderDto headerDto) {
        var result = checkFoTransactionHeaderDto(headerDto);

        if (!result) {
            return headerDto.getTotalPaymentFailed().toString();
        }
        return null;
    }

    protected String setTotalFailedRecord(FoTransactionHeaderDto headerDto) {
        var result = checkFoTransactionHeaderDto(headerDto);

        if (!result) {
            return headerDto.getTotalInvoiceFailed().toString();
        }
        return null;
    }

    protected String setTypePayment(FoTransactionHeaderEntity headerEntity) {
        String result = null;

        if (headerEntity.getStatus().equals(StatusEnum.PARTIALLY_SUCCESSFUL)) {
            if (headerEntity.getTotalCreditNoteAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = "Partially-CN";
            } else if (headerEntity.getTotalAccountTransferAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = "Partially-ACC";
            }
        } else {
            if (headerEntity.getTotalAccountTransferAmount().compareTo(BigDecimal.ZERO) > 0 && headerEntity.getTotalCreditNoteAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = "CN and ACC";
            } else if (headerEntity.getTotalCreditNoteAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = "CN";
            } else if (headerEntity.getTotalAccountTransferAmount().compareTo(BigDecimal.ZERO) > 0) {
                result = "ACC";
            }
        }

        return result;
    }

    protected String setStreamTransactionCode(FoTransactionHeaderEntity foTransactionHeaderEntity) {
        if (foTransactionHeaderEntity.getTransactionName().equals(Constant.UPLOAD_INVOICE)) {
            return foTransactionHeaderEntity.getChainingId();
        }
        return foTransactionHeaderEntity.getFoTransactionHeaderId();
    }
}
