package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.UserPrinciple;
import com.github.joostvdg.keepwatching.service.WatcherService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;

@RestController
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WatcherService watcherService;

    public UserController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @RequestMapping("/user")
    public ResponseEntity<UserPrinciple> user(@AuthenticationPrincipal OAuth2User principal) {
        logger.info("User::GET");

        Object objName = principal.getAttributes().get("name");
        if (objName == null) {
            objName = principal.getAttributes().get("login");
        }

        UserPrinciple userPrinciple = new UserPrinciple(principal.getName(), objName.toString());
        return ResponseEntity.ok().body(userPrinciple);

    }

    @RequestMapping("/authenticated")
    public boolean authenticated(@AuthenticationPrincipal OAuth2User principal) {
        logger.info("Authenticated::GET");
        if (principal != null) {
            logger.info("adding new watcher if not exists");
            var map = principal.getAttributes();
            for (var entry : map.entrySet()) {
                if(entry.getValue() != null)
                    logger.info(entry.getKey() + "/" + entry.getValue().toString());
            }  
            
                        
            Object objName = principal.getAttributes().get("name");
            if (objName == null) {
                objName = principal.getAttributes().get("login");
            }
            String identifier = principal.getName();
            watcherService.addNewWatcherIfNotExists(identifier, objName.toString());
            return true;
        }
        return false;
    }

}
