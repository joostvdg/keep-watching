package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.WatchListShare;

import java.util.List;

public interface WatchListService {

    List<WatchList> getAllWatchLists(Watcher watcher);

    /**
     * Creates a new WatchList with the watcher as the owner.
     * @param watchList the watchList to create
     * @param watcher the owner of the watchList
     * @return the created watchList, which will now include its id
     */
    WatchList newWatchList(WatchList watchList, Watcher watcher);

    WatchList getWatchListById(Long id, Watcher watcher);

    boolean updateWatchList(WatchList watchList, Watcher watcher);

    boolean deleteWatchListById(Long id, Watcher watcher);

    WatchList getWatchListByName(String watchlistName, Watcher watcher);

    /**
     * To share a watchList to another watcher.
     * @param watchList the watchList to share
     * @param owner the watcher that owns the watchList to share
     * @param sharer the watcher to share the watchList with
     * @param hasWriteRights if the sharee has write rights on this watchList
     */
    void shareWatchList(WatchList watchList, Watcher owner, Watcher sharer, boolean hasWriteRights);

    /**
     * Return the list of watchers this watchlist is shared with.
     * @param watchList the watchlist for which to check the shared with list
     * @param owner the owner of the watchlist as check
     * @return the list of watchers to which this watchlist is shared
     */
    List<WatchListShare> getSharedWith(WatchList watchList, Watcher owner);
}
