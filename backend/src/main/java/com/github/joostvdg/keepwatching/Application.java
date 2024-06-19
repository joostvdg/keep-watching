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

@SpringBootApplication()
//@EnableSwagger2
//@ComponentScan(basePackages = "com.github.joostvdg.keepwatching")
@EnableTransactionManagement
//@EnableAutoConfiguration
//@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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

}
