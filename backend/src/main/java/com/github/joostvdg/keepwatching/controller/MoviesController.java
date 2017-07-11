package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.service.MovieService;
import com.github.joostvdg.keepwatching.service.WatcherService;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MovieService movieService;
    private ResponseEntity notAuthorizedResponse;

    public MoviesController(MovieService movieService) {
        this.movieService = movieService;
        notAuthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(
            value = {"/{id}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Movie> getMovieById(Principal principal, @PathVariable("id") long movieId){
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Movies::GET {}", movieId);
        return ResponseEntity.ok().body(movieService.getMovieById(movieId));
    }

    @RequestMapping(
            value = {""},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.PUT}
    )
    @ResponseBody
    public ResponseEntity<Movie> updateMovie(Principal principal, @ApiParam("Movie to update") @RequestBody Movie movie)  {
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Movies::PUT {}", movie.getName());
        movieService.updateMovie(movie);
        return ResponseEntity.ok().build();
    }

}
