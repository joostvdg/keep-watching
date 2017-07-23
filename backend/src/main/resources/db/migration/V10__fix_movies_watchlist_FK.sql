ALTER TABLE Movies DROP COLUMN watchlist_id;
DROP TABLE Movies_Of_Watchlists CASCADE;
TRUNCATE TABLE Movies CASCADE;
ALTER TABLE Movies
    ADD COLUMN watchlist_id
    integer REFERENCES Watchlist (id)
    NOT NULL;
