package com.github.joostvdg.keepwatching.service.impl;


import com.github.joostvdg.keepwatching.model.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getAllMovies();

    Movie newMovie(Movie movie);

    Movie getMovieById(Long id);

    void deleteMovieById(Long id);
}
