ALTER TABLE Movies
    ADD COLUMN watchlist_id
    integer REFERENCES Movies (id)
    NOT NULL DEFAULT 1;

ALTER TABLE Movies ALTER COLUMN watchlist_id DROP DEFAULT;