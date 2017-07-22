package com.github.joostvdg.keepwatching.model;

public class WatchListShare {
    private final WatchList watchList;
    private final Watcher sharedWith;
    private final boolean writeAccess;

    public WatchListShare(WatchList watchList, Watcher sharedWith, boolean writeAccess) {
        this.watchList = watchList;
        this.sharedWith = sharedWith;
        this.writeAccess = writeAccess;
    }

    public WatchList getWatchList() {
        return watchList;
    }

    public Watcher getSharedWith() {
        return sharedWith;
    }

    public boolean isWriteAccess() {
        return writeAccess;
    }
}
