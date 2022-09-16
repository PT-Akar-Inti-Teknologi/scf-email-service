package bca.mbb.config;

import bca.mbb.mbbcommonlib.logger.LoggingInterceptor;
import bca.mbb.mbbcommonlib.user_detail.UserDetailsFromHeaderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  LoggingInterceptor loggingInterceptor;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new UserDetailsFromHeaderConverter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loggingInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    long maxAgeSecs = 3600;
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
            .maxAge(maxAgeSecs);
  }

}
