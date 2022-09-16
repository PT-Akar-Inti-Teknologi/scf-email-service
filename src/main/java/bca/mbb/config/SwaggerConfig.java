package bca.mbb.config;

import bca.mbb.mbbcommonlib.constant.MBBConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.sql.Timestamp;
import java.util.*;

@Configuration
public class SwaggerConfig {
    public static final String AUTHORIZATION_HEADER = "x-actor-id";
    private static final String PRODUCE_TYPE = "application/json";


    @Bean
    public Docket generalApi() {
        MBBConstant.apiInternalToken = String.valueOf(UUID.randomUUID());
        Set<String> responseProduceType = new HashSet<>();
        responseProduceType.add(PRODUCE_TYPE);
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any()).build()
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ResponseEntity.class)
                .produces(responseProduceType)
                .consumes(responseProduceType)
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(actorId()))
                .groupName("fo-service")
                .apiInfo(apiInfo()).directModelSubstitute(Timestamp.class, Long.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("FO Invoice Menagement Service")
                .version("0.3.0")
                .contact(new Contact("MyBCA Business", "https://www.bca.co.id/mybca", "halo@bca.co.id"))
                .license("Privacy Policy")
                .licenseUrl("https://ibank.klikbca.com/privacy.htm")
                .description("MyBCA Business SCF Service for Front Office")
                .build();
    }

    private ApiKey actorId() {
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        var authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        var authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes));
    }

}
