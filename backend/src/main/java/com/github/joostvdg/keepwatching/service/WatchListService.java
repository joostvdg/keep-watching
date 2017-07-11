package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;

import java.util.List;

public interface WatchListService {

    List<WatchList> getAllWatchLists(Watcher watcher);

    WatchList newWatchList(WatchList watchList, Watcher watcher);

    WatchList getWatchListById(Long id, Watcher watcher);

    boolean updateWatchList(WatchList watchList, Watcher watcher);

    void deleteWatchListById(Long id, Watcher watcher);

    WatchList getWatchListByName(String watchlistName, Watcher watcher);
}
