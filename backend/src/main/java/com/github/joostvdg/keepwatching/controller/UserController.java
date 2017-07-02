package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.UserPrinciple;
import com.github.joostvdg.keepwatching.service.WatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;

@RestController
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WatcherService watcherService;

    public UserController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @RequestMapping("/user")
    public ResponseEntity<UserPrinciple> user(Principal principal) {
        logger.info("User::GET");

        if (principal != null && principal instanceof OAuth2Authentication && ((OAuth2Authentication) principal).getUserAuthentication().getDetails() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) principal;
            String principle = auth.getPrincipal().toString();
            String name = "" ;
            if (auth.getDetails() instanceof LinkedHashMap) {
                Object valueObject = ((LinkedHashMap) auth.getDetails()).get("name" );
                if (valueObject != null) {
                    name = valueObject.toString();
                }
            }
            UserPrinciple userPrinciple = new UserPrinciple(name, principle);
            return ResponseEntity.ok().body(userPrinciple);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/authenticated")
    public boolean authenticated(Principal principal) {
        logger.info("Authenticated::GET");

        if (principal != null) {
            logger.info(principal.toString());
            logger.info(String.format("Class=%s", principal.getClass().getSimpleName()));
            if (principal instanceof OAuth2Authentication) {
                OAuth2Authentication auth = (OAuth2Authentication) principal;
                if (auth.getPrincipal() != null) {
                    logger.info(auth.getPrincipal().getClass().getSimpleName());
                    logger.info(auth.getPrincipal().toString());
                    watcherService.addNewWatcherIfNotExists(auth.getPrincipal().toString());
                }
                if (auth.getUserAuthentication() != null) {
                    logger.info(auth.getUserAuthentication().toString());
                    if (auth.getUserAuthentication().getDetails() != null) {
                        logger.info(auth.getUserAuthentication().getDetails().getClass().getSimpleName());
                        logger.info(auth.getUserAuthentication().getDetails().toString() );
                    }
                    return auth.getUserAuthentication().isAuthenticated();
                }
            }
        }

        return false;
    }

}
