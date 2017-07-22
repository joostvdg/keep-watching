package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
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
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    public void shouldReturnMoviesForWatchListOne()  {
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        movie.setWatchListId(watchList.getId());
        Movie moviePersisted = movieService.newMovie(movie, watchList);
        List<Movie> movies = movieService.getAllMovies(watchList);
        assertNotNull(movies);
        assertNotNull(moviePersisted);
        assertNotNull(moviePersisted.getId());
        assertTrue(!movies.isEmpty());
    }

    @Test
    public void shouldReturnExistingMovie(){
        Long id = new Long(1);
        Movie movie = movieService.getMovieById(id);
        assertNotNull(movie);
        assertEquals("Logan", movie.getName());
        assertEquals(id, (Long)movie.getId());
        assertEquals("James Mangold", movie.getDirector());
        assertEquals("Hugh Jackman,Patrick Stewart", movie.getNotableActors());
        assertEquals("2017", movie.getReleaseYear());
        assertEquals("Action,Drama,Sci-Fi", movie.getGenre());
        assertEquals("http://www.imdb.com/title/tt3315342/", movie.getImdbLink());
        assertEquals(true, movie.isSeen());
        assertEquals(true, movie.isCinemaWorthy());
        assertEquals(true, movie.isWanted());
    }

    @Test
    public void shouldCreateNewMovie(){
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        movie.setWatchListId(watchList.getId());
        Movie moviePersisted = movieService.newMovie(movie, watchList);

        assertNotNull(moviePersisted);
        assertEquals(name, moviePersisted.getName());
        assertNotNull(moviePersisted.getId());
        assertTrue(moviePersisted.getId() > 0);
        Movie movieFound = movieService.getMovieById(moviePersisted.getId());
        assertNotNull(movieFound);
        assertEquals(name, movieFound.getName());
    }

}
