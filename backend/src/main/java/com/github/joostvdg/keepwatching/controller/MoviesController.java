package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.service.MovieService;
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

    @Autowired
    private MovieService movieService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getMovies(Principal principal){
        if (principal == null) {
            logger.warn("No principle");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Movies::GET");
        return ResponseEntity.ok().body(movieService.getAllMovies());
    }

    @RequestMapping(
            value = {"/{id}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public ResponseEntity<Movie> getMovieById(@PathVariable("id") long movieId){
        logger.info(String.format("Movies::GET %d", movieId));
        return ResponseEntity.ok().body(movieService.getMovieById(movieId));
    }

    @RequestMapping(
            value = {""},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseEntity<Movie> newMovie(@ApiParam("Movie to add") @RequestBody Movie movie)  {
        logger.info(String.format("Movies::POST %s", movie.getName()));
        return ResponseEntity.ok().body(movieService.newMovie(movie));
    }

    @RequestMapping(
            value = {""},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.PUT}
    )
    @ResponseBody
    public ResponseEntity<Movie> updateMovie(@ApiParam("Movie to update") @RequestBody Movie movie)  {
        logger.info(String.format("Movies::PUT %s", movie.getName()));
        logger.info(movie.toString());
        movieService.updateMovie(movie);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = {"/{movieId}"},
            produces = {"application/json", "text/plain; charset=utf-8"},
            method = {RequestMethod.DELETE}
    )
    @ResponseBody
    public ResponseEntity deleteMovieById(@PathVariable Long movieId)  {
        logger.info(String.format("Movies::DELETE %s", movieId));
        movieService.deleteMovieById(movieId);
        return ResponseEntity.ok().build();
    }
}
