package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.UserPrinciple;
import com.github.joostvdg.keepwatching.service.WatcherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WatcherService watcherService;

    public UserController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @Operation(summary = "Get user details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details"),
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content)
    })
    @RequestMapping("/api/user")
    public ResponseEntity<UserPrinciple> user(@AuthenticationPrincipal OAuth2User principal) {
        logger.info("User::GET");

        if (principal == null || principal.getAttributes() == null || principal.getAttributes().isEmpty()){
            return ResponseEntity.status(401).build();
        }

        Object objName = principal.getAttributes().get("name");
        if (objName == null) {
            objName = principal.getAttributes().get("login");
        }

        UserPrinciple userPrinciple = new UserPrinciple(principal.getName(), objName.toString());
        return ResponseEntity.ok().body(userPrinciple);

    }

    @Operation(summary = "Verifies the current user is authenticated, and returns CSRF token in response headers if authenticated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User is authenticated", content = {@Content(mediaType = "plain/text")}),
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content)
    })
    @RequestMapping("/authenticated")
    public ResponseEntity<Boolean> authenticated(@AuthenticationPrincipal OAuth2User principal) {
        logger.info("Authenticated::GET");
        if (principal != null) {
            if (watcherService.getWatcherByIdentifier(principal.getName()) != null) {
                return ResponseEntity.ok().body(true);
            }

            logger.info("adding new watcher if not exists");
            var map = principal.getAttributes();
            for (var entry : map.entrySet()) {
                if(entry.getValue() != null)
                    logger.info(entry.getKey() + "/" + entry.getValue());
            }

            Object objName = principal.getAttributes().get("name");
            if (objName == null) {
                objName = principal.getAttributes().get("login");
            }
            String identifier = principal.getName();
            try { // there can be a race condition here, but as long as the user is recorded and logged in, its fine
                watcherService.addNewWatcherIfNotExists(identifier, objName.toString());
            } catch (Exception e) {
                logger.error("Error adding new watcher", e);
            }
            return ResponseEntity.ok().body(true);
        }
        return ResponseEntity.status(401).build();
    }

}
