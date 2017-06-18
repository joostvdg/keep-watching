package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.service.WatchListService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by joost on 5-6-17.
 */
@Transactional
@Service("watchListService")
@Component
public class WatchListServiceImpl implements WatchListService {


    @Override
    public List<WatchList> getAllMovies(Watcher watcher) {
        return null;
    }

    @Override
    public WatchList newWatchList(WatchList watchList) {
        return null;
    }

    @Override
    public WatchList getWatchListById(Long id) {
        return null;
    }
}
