package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.tables.records.WatchlistRecord;
import com.github.joostvdg.keepwatching.service.WatchListService;
import com.github.joostvdg.keepwatching.service.WatcherService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.joostvdg.keepwatching.model.tables.Watchlist.WATCHLIST;

@Transactional
@Service("watchListService")
@Component
public class WatchListServiceImpl implements WatchListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // JOOQ DSL Context
    private DSLContext dsl;
    private WatcherService watcherService;

    public WatchListServiceImpl(DSLContext dsl, WatcherService watcherService) {
        this.dsl = dsl;
        this.dsl.configuration().set(SQLDialect.POSTGRES_9_5);
        this.watcherService = watcherService;
    }

    @Override
    public List<WatchList> getAllWatchLists(Watcher watcher) {
        List<WatchList> watchLists = new ArrayList<>();
        Long userId = watcher.getId();
        Result<Record> result = dsl.select().from(WATCHLIST).where(WATCHLIST.USER_ID.eq(userId.intValue())).fetch();
        for (Record r : result) {
            watchLists.add(getWatchListEntity(r));
        }
        return watchLists;
    }

    @Override
    public WatchList newWatchList(WatchList watchList, Watcher watcher) {
        assert watcher != null;
        assert watcher.getId() > 0;
        assert watchList != null;
        assert watchList.getName() != null;

        int userId = ((Long)watcher.getId()).intValue();
        WatchlistRecord watchlistRecord = dsl.insertInto(WATCHLIST)
                .set(WATCHLIST.NAME, watchList.getName())
                .set(WATCHLIST.USER_ID, userId)
                .returning(WATCHLIST.ID)
                .fetchOne();
        watchList.setOwner(watcher);
        watchList.setId(watchlistRecord.getId());
        return watchList;
    }

    @Override
    public WatchList getWatchListById(Long id,Watcher watcher) {
        assert id != null;
        assert id > 0;
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(id.intValue())).fetchOne();
        if (record == null || record.getValue(WATCHLIST.USER_ID, Long.class) != watcher.getId()) {
            return null;
        }
        return getWatchListEntity(record);
    }

    @Override
    public boolean updateWatchList(WatchList watchList, Watcher watcher) {
        assert watcher != null;
        assert watcher.getId() > 0;
        assert watchList != null;
        assert watchList.getName() != null;
        assert watchList.getId() > 0;

        int id = ((Long)watchList.getId()).intValue();
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(id)).fetchOne();
        if (record == null) {
            logger.warn("No valid value for the id ({}), so cannot update watchlist.", id);
            return false;
        }

        int userId = ((Long)watcher.getId()).intValue();
        int updateCount = dsl.update(WATCHLIST)
                .set(WATCHLIST.NAME, watchList.getName())
                .set(WATCHLIST.USER_ID, userId)
                .where(WATCHLIST.ID.eq(id))
                .execute();
        return updateCount == 1;
    }

    @Override
    public void deleteWatchListById(Long id, Watcher watcher) {
        // TODO: allow only "owned"  watch lists to be deleted
        if (id == null || id < 1) {
            logger.warn("No valid value for the id ({}), so cannot delete.", id);
            return;
        }

        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(id.intValue())).fetchOne();
        if (record != null) {
            logger.info("Found for id {}, going to delete it.", id);
            dsl.delete(WATCHLIST)
                    .where(WATCHLIST.ID.eq(id.intValue()))
                    .execute();
        } else {
            logger.warn("Did not find a watchlist with id {}, so cannot delete.", id);
        }
    }

    private WatchList getWatchListEntity(Record record){
        Long id = record.getValue(WATCHLIST.ID, Long.class);
        Long userId = record.getValue(WATCHLIST.USER_ID, Long.class);
        String name = record.getValue(WATCHLIST.NAME, String.class);

        WatchList watchList = new WatchList();
        watchList.setId(id);
        watchList.setName(name);
        Watcher watcher = watcherService.getWatcherById(userId);
        watchList.setOwner(watcher);
        return watchList;
    }

}
