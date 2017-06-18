package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    public void findAllMovies()  {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertTrue(!movies.isEmpty());
        for (Movie movie : movies) {
            System.err.println(movie);
        }

        assertEquals("Logan", movies.get(0).getName());
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
        assertEquals(LocalDate.of(2017, Month.MARCH, 2), movie.getReleaseDate());// '2017-03-02'
        assertEquals(false, movie.isSeen());
        assertEquals(true, movie.isCinemaWorthy());
        assertEquals(true, movie.isWanted());
    }

    @Test
    public void shouldCreateNewMovie(){
        String name = "John Wick 2";
        Movie movie = new Movie(name);
        Movie moviePersisted = movieService.newMovie(movie);

        assertNotNull(moviePersisted);
        assertEquals(name, moviePersisted.getName());
        assertNotNull(moviePersisted.getId());
        assertTrue(moviePersisted.getId() > 0);
        Movie movieFound = movieService.getMovieById(moviePersisted.getId());
        assertNotNull(movieFound);
        assertEquals(name, movieFound.getName());
    }

}
