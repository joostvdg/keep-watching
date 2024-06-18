package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.WatchListShare;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.external.WatchListShareDTO;
import com.github.joostvdg.keepwatching.service.MovieService;
import com.github.joostvdg.keepwatching.service.WatchListService;
import com.github.joostvdg.keepwatching.service.WatcherService;
//import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public ResponseEntity<WatchList> newWatchList(@AuthenticationPrincipal OAuth2User principal, @RequestBody WatchList watchList) {
        logger.info("WatchList::POST");
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        return  ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "",
            produces = { "application/json", "text/plain; charset=utf-8" },
            method = RequestMethod.POST)
    public ResponseEntity<WatchList> updateWatchList(@AuthenticationPrincipal OAuth2User principal, @RequestBody WatchList watchList) {
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
    public ResponseEntity<Collection<WatchList>> getWatchList(@AuthenticationPrincipal OAuth2User principal){
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
    public ResponseEntity<WatchList> getWatchListById(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId){
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
    public ResponseEntity deleteWatchListById(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") Long watchListId)  {
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
    public ResponseEntity<Movie> newMovie(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId, @RequestBody Movie movie)  {
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Watchlist::Movies::PUT {}", movie.getName());
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        return ResponseEntity.ok().body(movieService.newMovie(movie, watchList));
    }

    @RequestMapping(
            value = {"/{watchListId}/movies"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getMovies(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId){
        if (principal == null) {return notAuthorizedResponse;}
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        logger.info("Watchlist::Movies::GET");
        return ResponseEntity.ok().body(movieService.getAllMovies(watchList));
    }

    @RequestMapping(
        value = {"/{watchListId}/shares"},
        produces = {"application/json", "text/plain; charset=utf-8"},
        method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Collection<WatchListShare>> getWatcherSharedWith(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId){
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Watchlist::Shares::GET");
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        return ResponseEntity.ok().body(watchListService.getSharedWith(watchList, watcher));
    }

    @RequestMapping(
        value = {"/{watchListId}/shares"},
        produces = {"application/json", "text/plain; charset=utf-8"},
        method = {RequestMethod.PUT}
    )
    @ResponseBody
    public ResponseEntity getWatcherSharedWith(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId, @RequestBody WatchListShareDTO watchListShare){
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Watchlist::Shares::PUT");

        assert watchListId > 0;
        assert watchListShare != null;
        assert watchListShare.getWatchListId() != null;
        assert watchListShare.getWatchListId().equals(watchListId);
        assert watchListShare.getWatchListId() > 0;
        assert watchListShare.getSharerIdentifier() != null;

        Watcher sharer = watcherService.getWatcherByIdentifier(watchListShare.getSharerIdentifier());
        Watcher owner = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, owner);
        boolean hasWriteRights = false;
        if (watchListShare.isHasWriteAccess()) {
            hasWriteRights = true;
        }

        assert sharer != null;
        assert owner != null;
        assert watchList != null;

        watchListService.shareWatchList(watchList, owner, sharer, hasWriteRights);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(
            value = {"/{watchListId}/movies/{movieId}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.DELETE}
    )
    @ResponseBody
    public ResponseEntity deleteMovieById(@AuthenticationPrincipal OAuth2User principal, @PathVariable("watchListId") long watchListId, @PathVariable Long movieId)  {
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Watchlist::Movies::DELETE {}", movieId);
        Watcher watcher = watcherService.getWatcherFromPrincipal(principal);
        WatchList watchList = watchListService.getWatchListById(watchListId, watcher);
        movieService.deleteMovieById(movieId, watchListId);
        return ResponseEntity.ok().build();
    }

}
