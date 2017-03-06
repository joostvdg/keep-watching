package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Movie;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.joostvdg.keepwatching.model.tables.Movies.MOVIES;

@Transactional
@Service("movieService")
@Component
public class MovieService {

    // JOOQ DSL Context
    @Autowired
    private DSLContext dsl;

    public List<Movie> getAllMovies(){
        List<Movie> movies = new ArrayList<>();
        Result<Record> result = dsl.select().from(MOVIES).fetch();
        for (Record r : result) {
            movies.add(getMovieEntity(r));
        }
        return movies;
    }

    private Movie getMovieEntity(Record r){
        Integer id = r.getValue(MOVIES.ID, Integer.class);
        String name = r.getValue(MOVIES.MOVIE_NAME, String.class);
        return new Movie(id, name);
    }

}
