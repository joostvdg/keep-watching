package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.service.WatchListService;
import com.github.joostvdg.keepwatching.service.WatcherService;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/watchlist")
public class WatchListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WatchListService watchListService;
    private WatcherService watcherService;

    private static ResponseEntity notAuthorizedResponse;

    public WatchListController(WatchListService watchListService, WatcherService watcherService) {
        this.watchListService = watchListService;
        this.watcherService = watcherService;
        notAuthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(value = "",
            produces = { "application/json", "text/plain; charset=utf-8" },
            method = RequestMethod.POST)
    public ResponseEntity<WatchList> newWatchList(Principal principal, @ApiParam("WatchList to add") @RequestBody WatchList watchList) {
        logger.info("WatchList::POST");
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        return  ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "",
            produces = { "application/json", "text/plain; charset=utf-8" },
            method = RequestMethod.PUT)
    public ResponseEntity<WatchList> updateWatchList(Principal principal, @ApiParam("WatchList to update") @RequestBody WatchList watchList) {
        logger.info("WatchList::PUT");
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        boolean isUpdated = watchListService.updateWatchList(watchList, watcher);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<WatchList>> getWatchList(Principal principal){
        logger.info("WatchList::GET");
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        return ResponseEntity.ok().body(watchListService.getAllWatchLists(watcher));
    }

    @RequestMapping(
            value = {"/{watchListId}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<WatchList> getWatchListById(Principal principal, @PathVariable("watchListId") long watchListId){
        logger.info("WatchList::GET {}", watchListId);
        if (principal == null) {return notAuthorizedResponse;}
        return ResponseEntity.ok().body(watchListService.getWatchListById(watchListId));
    }

    @RequestMapping(
            value = {"/{watchListId}"},
            method = {RequestMethod.DELETE}
    )
    @ResponseBody
    public ResponseEntity deleteMovieById(Principal principal, @PathVariable("watchListId") Long watchListId)  {
        logger.info("WatchList::DELETE {}", watchListId);
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        watchListService.deleteWatchListById(watchListId, watcher);
        return ResponseEntity.ok().build();
    }

}
