-- Create tables
CREATE TABLE IF NOT EXISTS Movies_Of_Watchlists
(
    id SERIAL PRIMARY KEY,
    movie_id integer REFERENCES Movies (id),
    watchlist_id integer REFERENCES Watchlist (id)
);
