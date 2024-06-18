package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.Watcher;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Created by joost on 5-6-17.
 */
public interface WatcherService {

    List<Watcher> getAllWatchers();

    Watcher newWatcher(Watcher movie);

    Watcher getWatcherById(Long id);

    void addNewWatcherIfNotExists(String identifier, String name);

    Watcher getWatcherByIdentifier(String identifier);

    Watcher getWatcherFromPrincipal(OAuth2User  principal);

    // void deleteWatcherById(Long id);

    // void updateWatcher(Watcher watcher);
}
