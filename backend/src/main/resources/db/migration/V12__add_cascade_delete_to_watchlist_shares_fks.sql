-- Drop tables
DROP TABLE Watchlist_Shares;

-- Create tables
CREATE TABLE Watchlist_Shares
(
    id SERIAL PRIMARY KEY,
    watcher_id integer REFERENCES Watcher (id) ON DELETE CASCADE,
    watchlist_id integer REFERENCES Watchlist (id) ON DELETE CASCADE,
    write_access BOOLEAN NOT NULL DEFAULT FALSE
);
