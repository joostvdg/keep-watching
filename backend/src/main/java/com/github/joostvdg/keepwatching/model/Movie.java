package com.github.joostvdg.keepwatching.model;

import java.time.LocalDate;

/**
 * The type Movie.
 */
public class Movie {

    private long id;
    private String name;
    private String studio;
    private String director;
    private String notableActors;
    private String releaseYear;
    private LocalDate releaseDate;
    private boolean isSeen;
    private boolean isCinemaWorthy;
    private String genre;
    private boolean isWanted;
    private String imdbLink;

    /**
     * Instantiates a new Movie.
     */
    public Movie() {
    }

    /**
     * Instantiates a new Movie.
     *
     * @param name the name
     */
    public Movie(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new Movie.
     *
     * @param id   the id
     * @param name the name
     */
    public Movie(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets studio.
     *
     * @return the studio
     */
    public String getStudio() {
        return studio;
    }


    /**
     * Gets director.
     *
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets director.
     *
     * @param director the director
     */
    public void setDirector(String director) {
        this.director = director;
    }


    /**
     * Sets studio.
     *
     * @param studio the studio
     */
    public void setStudio(String studio) {
        this.studio = studio;
    }

    /**
     * Gets notable actors.
     *
     * @return the notable actors
     */
    public String getNotableActors() {
        return notableActors;
    }

    /**
     * Sets notable actors.
     *
     * @param notableActors the notable actors
     */
    public void setNotableActors(String notableActors) {
        this.notableActors = notableActors;
    }

    /**
     * Gets release year.
     *
     * @return the release year
     */
    public String getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets release year.
     *
     * @param releaseYear the release year
     */
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets release date.
     *
     * @return the release date
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets release date.
     *
     * @param releaseDate the release date
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Is seen boolean.
     *
     * @return the boolean
     */
    public boolean isSeen() {
        return isSeen;
    }

    /**
     * Sets seen.
     *
     * @param seen the seen
     */
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    /**
     * Is cinema worthy boolean.
     *
     * @return the boolean
     */
    public boolean isCinemaWorthy() {
        return isCinemaWorthy;
    }

    /**
     * Sets cinema worthy.
     *
     * @param cinemaWorthy the cinema worthy
     */
    public void setCinemaWorthy(boolean cinemaWorthy) {
        isCinemaWorthy = cinemaWorthy;
    }

    /**
     * Gets genre.
     *
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets genre.
     *
     * @param genre the genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Is wanted boolean.
     *
     * @return the boolean
     */
    public boolean isWanted() {
        return isWanted;
    }

    /**
     * Sets wanted.
     *
     * @param wanted the wanted
     */
    public void setWanted(boolean wanted) {
        isWanted = wanted;
    }

    /**
     * Gets imdb link.
     *
     * @return the imdb link
     */
    public String getImdbLink() {
        return imdbLink;
    }

    /**
     * Sets imdb link.
     *
     * @param imdbLink the imdb link
     */
    public void setImdbLink(String imdbLink) {
        this.imdbLink = imdbLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return getId() == movie.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", director='" + director + '\'' +
                ", studio='" + studio + '\'' +
                ", notableActors='" + notableActors + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", releaseDate=" + releaseDate +
                ", isSeen=" + isSeen +
                ", isCinemaWorthy=" + isCinemaWorthy +
                ", genre='" + genre + '\'' +
                ", isWanted=" + isWanted +
                ", imdbLink='" + imdbLink + '\'' +
                '}';
    }
}
