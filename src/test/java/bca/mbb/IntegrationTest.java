package bca.mbb;

import bca.mbb.mbbcommonlib.constant.MBBConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration")
public class IntegrationTest {
    @Autowired
    protected MockMvc mvc;

    protected MockHttpServletRequestBuilder addUserDetailsHeader(
            MockHttpServletRequestBuilder builder, String corpId, String userId
    ) {
        return builder.header(MBBConstant.USER_DETAILS,
                String.format("{\"corpId\":\"%s\", \"userId\":\"%s\", \"role\":\"%s\",\"scopes\": [\"%s\"]}}"
                        , corpId, userId, MBBConstant.RoleEnum.MAKER_RELEASER.toString(),
                        MBBConstant.ScopeEnum.EXECUTE.toString() + "\", \"" + MBBConstant.ScopeEnum.AUTHORIZE.toString()));
    }

    protected MockHttpServletRequestBuilder addUserDetailsHeaderMaker(MockHttpServletRequestBuilder builder, String corpId, String userId) {
        return builder.header(MBBConstant.USER_DETAILS,
                String.format("{\"corpId\":\"%s\", \"userId\":\"%s\", \"role\":\"%s\",\"scopes\": [\"%s\"]}}"
                        , corpId, userId, MBBConstant.RoleEnum.MAKER.toString(), MBBConstant.ScopeEnum.CREATE.toString()));
    }

    protected MockHttpServletRequestBuilder addUserDetailsHeaderReleaser(MockHttpServletRequestBuilder builder, String corpId, String userId) {
        return builder.header(MBBConstant.USER_DETAILS,
                String.format("{\"corpId\":\"%s\", \"userId\":\"%s\", \"role\":\"%s\",\"scopes\": [\"%s\"]}"
                        , corpId, userId, MBBConstant.RoleEnum.RELEASER.toString(), MBBConstant.ScopeEnum.AUTHORIZE.toString()));
    }

    protected MockHttpServletRequestBuilder addUserDetailsHeader(MockHttpServletRequestBuilder builder) {
        return addUserDetailsHeader(builder, "CORPA", "user-id");
    }

    protected <T> T mapStringToObject(Class<T> expectedResultClass, String jsonString) throws IOException {
        return new ObjectMapper().readValue(jsonString, expectedResultClass);
    }

}
