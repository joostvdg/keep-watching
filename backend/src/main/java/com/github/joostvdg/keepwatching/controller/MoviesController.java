package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.service.impl.MovieService;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MovieService movieService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getMovies(){
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
    public ResponseEntity<Movie> newWatchItem(@ApiParam("Movie to add") @RequestBody Movie movie)  {
        logger.info(String.format("Movies::POST %s", movie.getName()));
        return ResponseEntity.ok().body(movieService.newMovie(movie));
    }

}
