package bca.mbb.mapper;

import bca.mbb.dto.ApiResponse;
import bca.mbb.dto.InvoiceError;
import bca.mbb.util.CommonUtil;
import bca.mbb.util.Constant;
import lib.fo.entity.FoInvoiceErrorDetailEntity;
import lib.fo.entity.FoTransactionDetailEntity;
import lib.fo.entity.FoTransactionHeaderEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mapper(
        builder = @Builder(disableBuilder = true),
        imports = {Constant.class, CommonUtil.class, LocalDateTime.class}
)
public abstract class FoInvoiceErrorDetailEntityMapper {
    public static final FoInvoiceErrorDetailEntityMapper INSTANCE = Mappers.getMapper(FoInvoiceErrorDetailEntityMapper.class);

    @Mapping(target = "foInvoiceErrorDetailId", expression = "java(CommonUtil.uuid())")
    @Mapping(target = "chainingId", expression = "java(isNotify ? chainingId : foTransactionHeader.getChainingId())")
    @Mapping(target = "line", ignore = true)
    @Mapping(target = "errorCode", expression = "java(isNotify ? invoiceError.getErrorCode() : response.getErrorCode())")
    @Mapping(target = "createdDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "errorDescriptionEng", expression = "java(isNotify ? invoiceError.getErrorDescEng() : response.getErrorMessageEn())")
    @Mapping(target = "errorDescriptionInd", expression = "java(isNotify ? invoiceError.getErrorDescInd() : response.getErrorMessageInd())")
    public abstract FoInvoiceErrorDetailEntity from(boolean isNotify, String chainingId, InvoiceError invoiceError, ApiResponse response, FoTransactionHeaderEntity foTransactionHeader, List<FoTransactionDetailEntity> foTransactionDetailEntityList);

    @AfterMapping
    protected void getLine(@MappingTarget FoInvoiceErrorDetailEntity foInvoiceErrorDetailEntity, boolean isNotify, InvoiceError invoiceError, List<FoTransactionDetailEntity> foDetails) {
        foInvoiceErrorDetailEntity.setLine(isNotify ? invoiceError.getLine() : IntStream.range(1, (foDetails.size() + 1)).boxed().map(String::valueOf).collect(Collectors.joining(",")));
    }
}
