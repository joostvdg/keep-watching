package com.github.joostvdg.keepwatching.service;


import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;

import java.util.List;

public interface MovieService {

    List<Movie> getAllMovies(WatchList watchList);

    Movie newMovie(Movie movie, WatchList watchList);

    Movie getMovieById(Long id);

    void deleteMovieById(Long id, Long watchListId);

    void updateMovie(Movie movie);
}
