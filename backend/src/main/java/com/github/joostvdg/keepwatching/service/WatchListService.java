package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;

import java.util.List;

/**
 * Created by joost on 5-6-17.
 */
public interface WatchListService {

    List<WatchList> getAllMovies(Watcher watcher);

    WatchList newWatchList(WatchList watchList);

    WatchList getWatchListById(Long id);

    // void deleteWatchListById(Long id);

    // void updateWatchList(WatchList watchList);
}
