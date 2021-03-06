package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Watcher;

import java.security.Principal;
import java.util.List;

/**
 * Created by joost on 5-6-17.
 */
public interface WatcherService {

    List<Watcher> getAllWatchers();

    Watcher newWatcher(Watcher movie);

    Watcher getWatcherById(Long id);

    void addNewWatcherIfNotExists(String identifier);

    Watcher getWatcherByIdentifier(String identifier);

    Watcher getWatcherFromPrincipal(Principal principal);

    // void deleteWatcherById(Long id);

    // void updateWatcher(Watcher watcher);
}
