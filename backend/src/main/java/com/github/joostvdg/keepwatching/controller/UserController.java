package com.github.joostvdg.keepwatching.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/authenticated")
    public boolean user(Principal principal) {
        logger.info("User::GET");
        return principal != null;
    }

}
