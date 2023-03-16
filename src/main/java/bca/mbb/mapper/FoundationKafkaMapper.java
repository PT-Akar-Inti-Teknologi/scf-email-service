package bca.mbb.mapper;

import bca.mbb.dto.Constant;
import bca.mbb.util.CommonUtil;
import lib.fo.dto.UserDetailDto;
import lib.fo.dto.foundation.FoundationKafkaSampleDto;
import lib.fo.entity.FoTransactionHeaderEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        builder = @Builder(disableBuilder = true),
        imports = {Constant.class, CommonUtil.class}
)
public abstract class FoundationKafkaMapper {
    public static final FoundationKafkaMapper INSTANCE = Mappers.getMapper(FoundationKafkaMapper.class);

    @Mapping(target = "corpId", source = "foTransactionHeader.corporateCode")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "transactionAmount", source = "foTransactionHeader.totalAmount")
    @Mapping(target = "transactionCurrency", source = "currency")
    @Mapping(target = "transactionDetails", ignore = true)
    @Mapping(target = "transactionEffectiveDate", expression = "java(CommonUtil.convertDateToString(null, foTransactionHeader.getEffectiveDate(), Constant.PATTERN_DATE_FOUNDATION))")
    @Mapping(target = "transactionStatus", source = "foTransactionHeader.status")
    @Mapping(target = "transactionType", expression = "java(Constant.LOAN_UPLOAD_INVOICE)")
    @Mapping(target = "streamTransactionId", source = "foTransactionHeader.chainingId")
    @Mapping(target = "sourceAccountCurrency", constant = "-")
    @Mapping(target = "sourceAccountName", constant = "-")
    @Mapping(target = "sourceAccountNumber", constant = "-")
    @Mapping(target = "sourceAccountType", constant = "-")
    @Mapping(target = "corpDailyLimitBookId", constant = "-")
    @Mapping(target = "uploadDate", ignore = true)
    @Mapping(target = "rejectCancelReason", source = "foTransactionHeader.reason")
    @Mapping(target = "createdAt", ignore = true)
    public abstract FoundationKafkaSampleDto from(String userId, FoTransactionHeaderEntity foTransactionHeader, String currency);

    @AfterMapping
    protected void getTransactionDetails(@MappingTarget FoundationKafkaSampleDto foundationKafkaSampleDto, FoTransactionHeaderEntity foTransactionHeader) {
        foundationKafkaSampleDto.setTransactionDetails((foTransactionHeader.getTransactionType().equalsIgnoreCase(Constant.TRANSACTION_TYPE_ADD) ? Constant.ADD_DESCRIPTION : Constant.DELETE_DESCRIPTION) + " — " + foTransactionHeader.getRemarks());
    }
}
