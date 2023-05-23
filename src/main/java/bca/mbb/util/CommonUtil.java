package bca.mbb.util;

import lib.fo.enums.ActionEnum;
import lib.fo.enums.StatusEnum;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CommonUtil {

    public static String successCode;
    @Value("${success-code}")
    public synchronized void setSuccessCode(String successCode) {
        CommonUtil.successCode = successCode;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty() || str.equalsIgnoreCase(Constant.IS_NULL);
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        var result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static String localDateTimeToIndonesia(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss z", new Locale("id", "ID"));

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Jakarta"));

        return zonedDateTime.format(formatter);
    }

    public static String localDateTimeToEnglish(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Jakarta"));

        String formattedDateTime = zonedDateTime.format(formatter);

        var zoneTime = formattedDateTime.split(" ");

        return formattedDateTime.replace(zoneTime[zoneTime.length-1], zoneTime[zoneTime.length-1].replace("0", ""));
    }

    public static String statusTranslate(StatusEnum statusInput, boolean isEng) {
        if (isEng) {
            return statusInput.equals(StatusEnum.SUCCESS) ? Constant.WORDING_EMAIL_SUCCESS_EN : Constant.WORDING_EMAIL_FAILED_EN;
        }
        return statusInput.equals(StatusEnum.SUCCESS) ? Constant.WORDING_EMAIL_SUCCESS : Constant.WORDING_EMAIL_FAILED;
    }

    public static String typeTranslate(ActionEnum actionEnum, boolean isEng) {
        if (isEng) {
            return actionEnum.equals(ActionEnum.ADD) ? Constant.WORDING_EMAIL_ADD_EN : Constant.WORDING_EMAIL_DELETE_EN;
        }
        return actionEnum.equals(ActionEnum.ADD) ? Constant.WORDING_EMAIL_ADD : Constant.WORDING_EMAIL_DELETE;
    }

    public static String nominal(BigDecimal totalAmount) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(totalAmount);
    }

    public static String convertDateToString(LocalDateTime localDateTime, LocalDate localDate, String pattern) {
        if (localDateTime != null) {
            return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
        } else if (localDate != null) {
            return DateTimeFormatter.ofPattern(pattern).format(localDate);
        }
        return null;
    }
}
