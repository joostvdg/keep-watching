package com.github.joostvdg.keepwatching.controller;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.service.MovieService;
//import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin
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
    public ResponseEntity<Movie> getMovieById(@AuthenticationPrincipal OAuth2User principal, @PathVariable("id") long movieId){
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
    public ResponseEntity<Movie> updateMovie(@AuthenticationPrincipal OAuth2User principal, @RequestBody Movie movie)  {
        if (principal == null) {return notAuthorizedResponse;}
        logger.info("Movies::PUT {}", movie.getName());
        movieService.updateMovie(movie);
        return ResponseEntity.ok().build();
    }

}
