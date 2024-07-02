package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.service.WatcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by joost on 5-6-17.
 */

@RestController
@RequestMapping("/api/watchers")
@CrossOrigin
public class WatcherController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WatcherService watcherService;

    public WatcherController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @Operation(summary = "Get all watchers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All watchers")
    })
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<Watcher>> getWatchers(){
        logger.info("Watchers::GET");
        return ResponseEntity.ok().body(watcherService.getAllWatchers());
    }

    @Operation(summary = "Get watcher by Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Watcher found")
    })
    @RequestMapping(
            value = {"/{id}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Watcher> getWatcherById(@PathVariable("id") long watcherId){
        logger.info(String.format("Watchers::GET %d", watcherId));
        return ResponseEntity.ok().body(watcherService.getWatcherById(watcherId));
    }

    @Operation(summary = "Create new watcher")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Watcher created")
    })
    @RequestMapping(
            value = {""},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseEntity<Watcher> newWatcher(@RequestBody Watcher watcher)  {
        logger.info(String.format("Watchers::POST %s", watcher.getName()));
        return ResponseEntity.ok().body(watcherService.newWatcher(watcher));
    }
}
