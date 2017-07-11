package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.service.MovieService;
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
    private MovieService movieService;

    private ResponseEntity notAuthorizedResponse;

    // TODO: maybe I need to many services in one controller?!
    public WatchListController(WatchListService watchListService, WatcherService watcherService, MovieService movieService) {
        this.watchListService = watchListService;
        this.watcherService = watcherService;
        this.movieService = movieService;
        notAuthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(value = "",
            produces = { "application/json", "text/plain; charset=utf-8" },
            method = RequestMethod.PUT)
    public ResponseEntity<WatchList> newWatchList(Principal principal, @ApiParam("WatchList to add") @RequestBody WatchList watchList) {
        logger.info("WatchList::POST");
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        return  ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "",
            produces = { "application/json", "text/plain; charset=utf-8" },
            method = RequestMethod.POST)
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
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        return ResponseEntity.ok().body(watchListService.getWatchListById(watchListId, watcher));
    }

    @RequestMapping(
            value = {"/{watchListId}"},
            method = {RequestMethod.DELETE}
    )
    @ResponseBody
    public ResponseEntity deleteWatchListById(Principal principal, @PathVariable("watchListId") Long watchListId)  {
        logger.info("WatchList::DELETE {}", watchListId);
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        watchListService.deleteWatchListById(watchListId, watcher);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = {"/{watchListId}/movies"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.PUT}
    )
    @ResponseBody
    public ResponseEntity<Movie> newMovie(Principal principal, @PathVariable("watchListId") long watchListId, @ApiParam("Movie to add") @RequestBody Movie movie)  {
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        logger.info("Watchlist::Movies::PUT {}", movie.getName());
        return ResponseEntity.ok().body(movieService.newMovie(movie, watchList));
    }

    @RequestMapping(
            value = {"/{watchListId}/movies"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getMovies(Principal principal, @PathVariable("watchListId") long watchListId){
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        logger.info("Watchlist::Movies::GET");
        return ResponseEntity.ok().body(movieService.getAllMovies(watchList));
    }

    @RequestMapping(
            value = {"/{watchListId}/movies/{movieId}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.DELETE}
    )
    @ResponseBody
    public ResponseEntity deleteMovieById(Principal principal, @PathVariable("watchListId") long watchListId, @PathVariable Long movieId)  {
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Watchlist::Movies::DELETE {}", movieId);
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        movieService.deleteMovieById(movieId, watchListId);
        return ResponseEntity.ok().build();
    }

}
