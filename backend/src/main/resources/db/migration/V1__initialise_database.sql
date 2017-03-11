-- Create schemas

-- Create tables
CREATE TABLE IF NOT EXISTS Movies
(
    id SERIAL PRIMARY KEY,
    movie_name VARCHAR(255),
    producer VARCHAR(255),
    director VARCHAR(255),
    studio VARCHAR(255),
    notable_actors VARCHAR(255),
    release_year VARCHAR(4),
    release_date DATE,
    seen BOOLEAN,
    cinema_worthy BOOLEAN,
    genre VARCHAR(64),
    want BOOLEAN,
    imdb VARCHAR(255)
);


-- Create FKs

-- Create Indexes
