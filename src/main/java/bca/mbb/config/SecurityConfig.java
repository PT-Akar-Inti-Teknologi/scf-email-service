package bca.mbb.config;

import bca.mbb.mbbcommonlib.user_detail.UserDetailsFromHeaderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/webjars/**").anyRequest();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().permitAll()
            .and().httpBasic().disable().csrf().disable();
  }

  @Autowired
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new UserDetailsFromHeaderConverter());
  }
}
