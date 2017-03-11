package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.tables.records.MoviesRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MovieServiceImpl implements MovieService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // JOOQ DSL Context
    @Autowired
    private DSLContext dsl;

    @Override
    public List<Movie> getAllMovies(){
        List<Movie> movies = new ArrayList<>();
        Result<Record> result = dsl.select().from(MOVIES).fetch();
        for (Record r : result) {
            movies.add(getMovieEntity(r));
        }
        return movies;
    }

    @Override
    public Movie newMovie(Movie movie) {
        assert movie != null;
        assert movie.getName() != null;

        MoviesRecord moviesRecord = dsl.insertInto(MOVIES)
                .set(MOVIES.MOVIE_NAME, movie.getName())
                .returning(MOVIES.ID)
                .fetchOne();
        movie.setId(moviesRecord.getId());
        return movie;
    }

    @Override
    public Movie getMovieById(Long movieId){
        Record record = dsl.select().from(MOVIES).where(MOVIES.ID.eq(movieId)).fetchOne();
        logger.info(String.format("Found for id %d", movieId));
        logger.info(record.toString());
        return record == null ? null : getMovieEntity(record);
    }

    private Movie getMovieEntity(Record r){
        Long id = r.getValue(MOVIES.ID, Long.class);
        String name = r.getValue(MOVIES.MOVIE_NAME, String.class);
        return new Movie(id, name);
    }

}
