package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.Movie;
import com.github.joostvdg.keepwatching.model.tables.records.MoviesRecord;
import com.github.joostvdg.keepwatching.service.MovieService;
import com.github.joostvdg.keepwatching.service.WatcherService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.github.joostvdg.keepwatching.model.tables.Movies.MOVIES;

@Transactional
@Service("movieService")
@Component
public class MovieServiceImpl implements MovieService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // JOOQ DSL Context
    private DSLContext dsl;

    public MovieServiceImpl(DSLContext dsl) {
        this.dsl = dsl;
        this.dsl.configuration().set(SQLDialect.POSTGRES_9_5);
    }

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
        java.sql.Date releaseDate = null;
        if (movie.getReleaseDate() != null) {
            releaseDate = new Date(movie.getReleaseDate().toEpochDay());
        }

        MoviesRecord moviesRecord = dsl.insertInto(MOVIES)
                .set(MOVIES.MOVIE_NAME, movie.getName())
                .set(MOVIES.STUDIO, movie.getStudio())
                .set(MOVIES.DIRECTOR, movie.getDirector())
                .set(MOVIES.NOTABLE_ACTORS, movie.getNotableActors())
                .set(MOVIES.RELEASE_YEAR, movie.getReleaseYear())
                .set(MOVIES.RELEASE_DATE, releaseDate)
                .set(MOVIES.SEEN, movie.isSeen())
                .set(MOVIES.CINEMA_WORTHY, movie.isCinemaWorthy())
                .set(MOVIES.GENRE, movie.getGenre())
                .set(MOVIES.WANT, movie.isWanted())
                .set(MOVIES.IMDB, movie.getImdbLink())
                .returning(MOVIES.ID)
                .fetchOne();
        movie.setId(moviesRecord.getId());
        return movie;
    }

    @Override
    public Movie getMovieById(Long movieId){
        Record record = dsl.select().from(MOVIES).where(MOVIES.ID.eq(movieId.intValue())).fetchOne();
        logger.info(String.format("Found for id %d", movieId));
        logger.info(record.toString());
        return record == null ? null : getMovieEntity(record);
    }

    @java.lang.Override
    public void deleteMovieById(Long id) {
        if (id == null || id < 1) {
            logger.warn(String.format("No valid value for the id (%d), so cannot delete.", id));
            return;
        }

        Record record = dsl.select().from(MOVIES).where(MOVIES.ID.eq(id.intValue())).fetchOne();
        if (record != null) {
            logger.info(String.format("Found for id %d, going to delete it.", id));
            dsl.delete(MOVIES)
                    .where(MOVIES.ID.eq(id.intValue()))
                    .execute();
        } else {
            logger.warn(String.format("Did not find a movie with id %d, so cannot delete.", id));
        }
    }

    @Override
    public void updateMovie(Movie movie) {
        assert movie != null;
        assert movie.getName() != null;
        assert movie.getId() >= 0;
        java.sql.Date releaseDate = null;
        if (movie.getReleaseDate() != null) {
            releaseDate = new Date(movie.getReleaseDate().toEpochDay());
        }

        Long movieId = movie.getId();
        dsl.update(MOVIES)
                .set(MOVIES.MOVIE_NAME, movie.getName())
                .set(MOVIES.STUDIO, movie.getStudio())
                .set(MOVIES.DIRECTOR, movie.getDirector())
                .set(MOVIES.NOTABLE_ACTORS, movie.getNotableActors())
                .set(MOVIES.RELEASE_YEAR, movie.getReleaseYear())
                .set(MOVIES.RELEASE_DATE, releaseDate)
                .set(MOVIES.SEEN, movie.isSeen())
                .set(MOVIES.CINEMA_WORTHY, movie.isCinemaWorthy())
                .set(MOVIES.GENRE, movie.getGenre())
                .set(MOVIES.WANT, movie.isWanted())
                .set(MOVIES.IMDB, movie.getImdbLink())
                .where(MOVIES.ID.eq(movieId.intValue()))
        .execute();
    }

    private Movie getMovieEntity(Record record){
        Long id = record.getValue(MOVIES.ID, Long.class);
        String name = record.getValue(MOVIES.MOVIE_NAME, String.class);

        Movie movie = new Movie(id, name);
        movie.setDirector(record.getValue(MOVIES.DIRECTOR, String.class));
        movie.setStudio(record.getValue(MOVIES.STUDIO, String.class));
        movie.setNotableActors(record.getValue(MOVIES.NOTABLE_ACTORS, String.class));
        movie.setReleaseYear(record.getValue(MOVIES.RELEASE_YEAR, String.class));
        java.sql.Date releaseDateSql = record.getValue(MOVIES.RELEASE_DATE, java.sql.Date.class);
        if ( releaseDateSql != null) {
            movie.setReleaseDate(releaseDateSql.toLocalDate());
        }

        movie.setGenre(record.getValue(MOVIES.GENRE, String.class));
        movie.setImdbLink(record.getValue(MOVIES.IMDB, String.class));

        if (record.getValue(MOVIES.WANT, Boolean.class) != null) {
            movie.setWanted(record.getValue(MOVIES.WANT, Boolean.class));
        }

        if (record.getValue(MOVIES.CINEMA_WORTHY, Boolean.class) != null) {
            movie.setCinemaWorthy(record.getValue(MOVIES.CINEMA_WORTHY, Boolean.class));
        }
        if (record.getValue(MOVIES.SEEN, Boolean.class) != null) {
            movie.setSeen(record.getValue(MOVIES.SEEN, Boolean.class));
        }

        return movie;
    }

}
