package bca.mbb.config;

import bca.mbb.mbbcommonlib.logger.LoggingInterceptor;
import bca.mbb.mbbcommonlib.logger.MBBLogger;
import bca.mbb.mbbcommonlib.logger.slf4j.Slf4jMBBLogger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.Field;

import static java.util.Optional.ofNullable;

@Configuration
public class MBBLoggerConfiguration {

  @Bean
  @Scope("prototype")
  MBBLogger logger(InjectionPoint injectionPoint) {
    return Slf4jMBBLogger.getLogger(ofNullable(injectionPoint.getMethodParameter())
            .<Class<?>>map(MethodParameter::getContainingClass)
            .orElseGet(() ->
                    ofNullable(injectionPoint.getField())
                            .map(Field::getDeclaringClass)
                            .orElseThrow(IllegalArgumentException::new)
            )
    );
  }

  @Bean
  public LoggingInterceptor loggingInterceptor(MBBLogger logger, ConversionService conversionService) {
    return new LoggingInterceptor(logger);
  }
}
