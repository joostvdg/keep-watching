package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.tables.records.WatcherRecord;
import com.github.joostvdg.keepwatching.service.WatcherService;
import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.github.joostvdg.keepwatching.model.tables.Watcher.WATCHER;

/**
 * Created by joost on 5-6-17.
 */
@Transactional
@Service("watcherService")
@Component
public class WatcherServiceImpl implements WatcherService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, Watcher> cachedWatchers;

    // JOOQ DSL Context
    @Autowired DSLContext dsl;

    @PostConstruct
    public void init() {
        logger.info("WatcherService initialized");
        cachedWatchers = new HashMap<>();
    }

    @Override
    public List<Watcher> getAllWatchers() {
        List<Watcher> watchers = new ArrayList<>();
        Result<Record> result = dsl.select().from(WATCHER).fetch();
        for (Record r : result) {
            var watcher = getWatcherEntity(r);
            watchers.add(watcher);
            cachedWatchers.put(watcher.getIdentifier(), watcher);
        }
        return watchers;
    }

    @Override
    public Watcher newWatcher(Watcher watcher) {
        logger.info("New Watcher, Identifier={}", watcher.getIdentifier());
        assert watcher != null;
        assert watcher.getName() != null;

        // TODO: should this become a cache?
        // TODO: should we do an update instead?
        if (cachedWatchers.containsKey(watcher.getIdentifier())) {
            logger.info("Watcher found in cache");
            return cachedWatchers.get(watcher.getIdentifier());
        } else {
            logger.info("Watcher not found in cache - inserting into DB");
        }

        WatcherRecord watcherRecord = dsl.insertInto(WATCHER)
                .set(WATCHER.NAME, watcher.getName())
                .set(WATCHER.IDENTIFIER, watcher.getIdentifier())
                .returning(WATCHER.ID)
                .fetchOne();
        watcher.setId(watcherRecord.getId());
        cachedWatchers.put(watcher.getIdentifier(), watcher);
        return watcher;
    }

    @Override
    public Watcher getWatcherById(Long id) {
        assert id != null;
        assert id > 0;

        Record record = dsl.select().from(WATCHER).where(WATCHER.ID.eq(id.intValue())).fetchOne();
        logger.info("Found for id {}", id);
        return record == null ? null : getWatcherEntity(record);
    }

    @Override
    public void addNewWatcherIfNotExists(String identifier, String name) {
        if (cachedWatchers.containsKey(identifier)) {
            logger.info("Watcher already exists in cache");
            return;
        }

        Watcher watcher = getWatcherByIdentifier(identifier);
        if (watcher == null) {
            logger.info("Watcher not found. Adding new watcher");
            watcher = new Watcher();
            watcher.setIdentifier(identifier);
            watcher.setName(name);
            var insertedWatcher = newWatcher(watcher);
            if (insertedWatcher != null) {
                cachedWatchers.put(identifier, insertedWatcher);
            }
        }
    }

    @Override
    public Watcher getWatcherByIdentifier(String identifier) {
        assert identifier != null;

        if (cachedWatchers.containsKey(identifier)) {
            logger.info("Watcher found in cache");
            return cachedWatchers.get(identifier);
        }

        Record record = dsl.select().from(WATCHER).where(WATCHER.IDENTIFIER.eq(identifier)).fetchOne();
        if (record != null) {
            logger.info("Found for identifier {}", identifier);
            Watcher watcher = getWatcherEntity(record);
            cachedWatchers.put(identifier, watcher);
            return watcher;
        }
        return null;
    }

    @Override
    public Watcher getWatcherFromPrincipal(OAuth2User principal) {
        assert principal != null;
        Watcher watcher = null;

        String identifier = principal.getName();
        if (!StringUtils.isEmpty(identifier) ) {
            watcher = getWatcherByIdentifier(identifier);
        }

        return watcher;
    }


    private Watcher getWatcherEntity(Record record){
        Long id = record.getValue(WATCHER.ID, Long.class);
        String name = record.getValue(WATCHER.NAME, String.class);
        String identifier = record.getValue(WATCHER.IDENTIFIER, String.class);

        Watcher watcher = new Watcher();
        watcher.setId(id);
        watcher.setName(name);
        watcher.setIdentifier(identifier);
        return watcher;
    }

    void clearCache() {
        cachedWatchers.clear();
    }
}
