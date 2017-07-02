DROP TABLE IF EXISTS WatchList;

CREATE TABLE IF NOT EXISTS Watchlist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    user_id int4 REFERENCES Watcher(id)
);
