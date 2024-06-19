package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
//import org.junit.Test;
//import org.junit.runner.RunWith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

//import static org.junit.Assert.*;

//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class MoviesServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private WatchListService watchListService;

    @Autowired
    private WatcherService watcherService;

    private WatchList watchList;

    @PostConstruct
    public void setup(){
        String identifier = "12345";
        Watcher watcher = watcherService.getWatcherByIdentifier(identifier);
        if (watcher == null) {
            watcher = new Watcher();
            watcher.setName("Pietje");
            watcher.setIdentifier(identifier);
            watcher = watcherService.newWatcher(watcher);
        }

        String watchlistName = "list1";
        watchList = watchListService.getWatchListByName(watchlistName, watcher);
        if (watchList == null) {
            watchList = new WatchList();
            watchList.setName(watchlistName);
            watchList.setOwner(watcher);
            watchList = watchListService.newWatchList(watchList, watcher);
        }
    }

    @Test
    public void findAllMovies()  {
        List<Movie> movies = movieService.getAllMovies(watchList);
        Assertions.assertNotNull(movies);
        Assertions.assertTrue(movies.isEmpty());
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        movie.setWatchListId(watchList.getId());
        movieService.newMovie(movie, watchList);
        movies = movieService.getAllMovies(watchList);
        Assertions.assertNotNull(movies);
        Assertions.assertFalse(movies.isEmpty());
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    public void shouldReturnMoviesForWatchListOne()  {
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        movie.setWatchListId(watchList.getId());
        Movie moviePersisted = movieService.newMovie(movie, watchList);
        List<Movie> movies = movieService.getAllMovies(watchList);
        Assertions.assertNotNull(movies);
        Assertions.assertNotNull(moviePersisted);
        Assertions.assertNotNull(moviePersisted.getId());
        Assertions.assertTrue(!movies.isEmpty());
    }

    @Test
    public void shouldCreateNewMovie(){
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        movie.setWatchListId(watchList.getId());
        Movie moviePersisted = movieService.newMovie(movie, watchList);

        Assertions.assertNotNull(moviePersisted);
        Assertions.assertEquals(name, moviePersisted.getName());
        Assertions.assertNotNull(moviePersisted.getId());
        Assertions.assertTrue(moviePersisted.getId() > 0);
        Movie movieFound = movieService.getMovieById(moviePersisted.getId());
        Assertions.assertNotNull(movieFound);
        Assertions.assertEquals(name, movieFound.getName());
    }

}
