package bca.mbb.util;

import bca.mbb.dto.TransactionDetailDto;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class CommonUtilTest {

    @Test
    void commonUtil() {
        assertTrue(StringUtils.isNotBlank(CommonUtil.uuid()));

        assertTrue(CommonUtil.isNullOrEmpty(null));
        assertTrue(CommonUtil.isNullOrEmpty(""));
        assertTrue(CommonUtil.isNullOrEmpty("null"));
        assertFalse(CommonUtil.isNullOrEmpty("test"));

        assertTrue(!ObjectUtils.isEmpty(CommonUtil.getNullPropertyNames(new TransactionDetailDto())));
    }
}