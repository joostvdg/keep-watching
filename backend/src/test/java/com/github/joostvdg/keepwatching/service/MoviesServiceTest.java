package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        for (Movie movie : movies)
        {
            System.err.println(movie);
        }

        assertEquals("Logan", movies.get(0).getName());
        assertEquals(0, movies.get(0).getId());
    }


}
