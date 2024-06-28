package com.github.joostvdg.keepwatching.config;

import com.github.joostvdg.keepwatching.controller.UserController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice(assignableTypes = UserController.class)
public class CsrfControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void getCsrfToken(HttpServletResponse response, CsrfToken csrfToken) {
        response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
    }
}
