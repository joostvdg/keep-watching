package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getTools(){
        logger.info("Movies::GET");
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1l, "Logan"));
        movies.add(new Movie(2l, "John Wick 2"));
        return ResponseEntity.ok().body(movies);
    }

    @Value("${user.password:default config}")
    private String config;

    @RequestMapping("/config")
    String getConfig() {
        logger.info("Config found is {}", config);
        return this.config;
    }

    @Value("${message:Hello default}")
    private String message;

    @RequestMapping("/message")
    String getMessage() {
        return this.message;
    }
}
