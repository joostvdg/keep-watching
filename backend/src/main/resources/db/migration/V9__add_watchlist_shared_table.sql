-- Create tables
CREATE TABLE Watchlist_Shares
(
    id SERIAL PRIMARY KEY,
    watcher_id integer REFERENCES Movies (id),
    watchlist_id integer REFERENCES Watchlist (id),
    write_access BOOLEAN NOT NULL DEFAULT FALSE
);
