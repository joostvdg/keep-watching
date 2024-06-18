package com.github.joostvdg.keepwatching;


import java.util.Collections;
import java.util.Map;

import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.Filter;

@SpringBootApplication
//@EnableSwagger2
//@ComponentScan(basePackages = "com.github.joostvdg.keepwatching")
@EnableTransactionManagement
//@EnableAutoConfiguration
//@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {

    // private OAuth2ClientContext oauth2ClientContext;

    // public Application(OAuth2ClientContext oauth2ClientContext){
    //     this.oauth2ClientContext = oauth2ClientContext;
    // }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .antMatcher("/**")
//            .authorizeRequests()
//                .antMatchers("/","/login**", "/webjars/**"," /view**", "/authenticated", "/js/**").permitAll()
//                .anyRequest().authenticated()
//            .and().logout().logoutSuccessUrl("/").permitAll()
//            .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            .and().csrf().ignoringAntMatchers("/logout");

        http
            .authorizeRequests(a -> a
                .antMatchers("/", "/error", "/js/**", "/webjars/**","/view**", "/authenticated").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .csrf(c -> c
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .logout(l -> l
                .logoutSuccessUrl("/").permitAll()
            )
            .oauth2Login();
    }

    // private Filter ssoFilter(OAuthClientResources client, String path) {
    //     OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
    //     OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
    //     filter.setRestTemplate(template);
    //     UserInfoTokenServices tokenServices = new UserInfoTokenServices(
    //             client.getResource().getUserInfoUri(),
    //             client.getClient().getClientId());
    //     tokenServices.setRestTemplate(template);
    //     filter.setTokenServices(tokenServices);
    //     return filter;
    // }

    // @Bean
    // @ConfigurationProperties("github")
    // public OAuthClientResources github() {
    //     return new OAuthClientResources();
    // }

    // @Bean
    // @ConfigurationProperties("facebook")
    // public OAuthClientResources facebook() {
    //     return new OAuthClientResources();
    // }

    // @Bean
    // public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
    //     FilterRegistrationBean registration = new FilterRegistrationBean();
    //     registration.setFilter(filter);
    //     registration.setOrder(-100);
    //     return registration;
    // }

    // private CsrfTokenRepository csrfTokenRepository() {
    //     HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    //     repository.setHeaderName("X-XSRF-TOKEN");
    //     return repository;
    // }

    @Bean
    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        SQLDialect dialect = SQLDialect.POSTGRES;
        jooqConfiguration.set(dialect);
        return jooqConfiguration;
    }
}
