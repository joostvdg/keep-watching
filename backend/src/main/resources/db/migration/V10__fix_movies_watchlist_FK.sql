ALTER TABLE Movies DROP COLUMN watchlist_id;
ALTER TABLE Movies
    ADD COLUMN watchlist_id
    integer REFERENCES Watchlist (id)
    NOT NULL DEFAULT 20;

ALTER TABLE Movies ALTER COLUMN watchlist_id DROP DEFAULT;