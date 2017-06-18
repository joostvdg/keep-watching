package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.tables.records.WatcherRecord;
import com.github.joostvdg.keepwatching.service.WatcherService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.joostvdg.keepwatching.model.tables.Watcher.WATCHER;

/**
 * Created by joost on 5-6-17.
 */
@Transactional
@Service("watcherService")
@Component
public class WatcherServiceImpl implements WatcherService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // JOOQ DSL Context
    @Autowired
    private DSLContext dsl;

    @Override
    public List<Watcher> getAllWatchers() {
        List<Watcher> watchers = new ArrayList<>();
        Result<Record> result = dsl.select().from(WATCHER).fetch();
        for (Record r : result) {
            watchers.add(getWatcherEntity(r));
        }
        return watchers;
    }

    @Override
    public Watcher newWatcher(Watcher watcher) {
        assert watcher != null;
        assert watcher.getName() != null;

        WatcherRecord watcherRecord = dsl.insertInto(WATCHER)
                .set(WATCHER.NAME, watcher.getName())
                .set(WATCHER.IDENTIFIER, watcher.getIdentifier())
                .returning(WATCHER.ID)
                .fetchOne();
        watcher.setId(watcherRecord.getId());
        return watcher;
    }

    @Override
    public Watcher getWatcherById(Long id) {
        Record record = dsl.select().from(WATCHER).where(WATCHER.ID.eq(id.intValue())).fetchOne();
        logger.info(String.format("Found for id %d", id));
        logger.info(record.toString());
        return record == null ? null : getWatcherEntity(record);
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
}
