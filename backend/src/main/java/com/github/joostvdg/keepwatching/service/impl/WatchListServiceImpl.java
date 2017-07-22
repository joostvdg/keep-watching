package com.github.joostvdg.keepwatching.service.impl;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.WatchListShare;
import com.github.joostvdg.keepwatching.model.Watcher;
import com.github.joostvdg.keepwatching.model.tables.WatchlistShares;
import com.github.joostvdg.keepwatching.model.tables.records.WatchlistRecord;
import com.github.joostvdg.keepwatching.service.WatchListService;
import com.github.joostvdg.keepwatching.service.WatcherService;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.joostvdg.keepwatching.model.tables.Watchlist.WATCHLIST;
import static com.github.joostvdg.keepwatching.model.tables.WatchlistShares.WATCHLIST_SHARES;

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

        Result<Record> result = dsl.select().from(WATCHLIST_SHARES).where(WATCHLIST_SHARES.WATCHER_ID.eq(userId.intValue())).fetch();
        List<Integer> sharedWatchListIds = new ArrayList<>();
        for (Record r : result) {
            Integer id = r.getValue(WATCHLIST_SHARES.WATCHLIST_ID, Long.class).intValue();
            sharedWatchListIds.add(id);
        }

        result = dsl.select().from(WATCHLIST).where(WATCHLIST.USER_ID.eq(userId.intValue())).or(WATCHLIST.ID.in(sharedWatchListIds)).fetch();
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
    public boolean deleteWatchListById(Long id, Watcher watcher) {
        assert watcher != null;
        assert watcher.getId() > 0;
        assert id != null;
        assert id > 0;

        boolean deleted = false;
        int watcherId = ((Long)watcher.getId()).intValue();
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(id.intValue())).and(WATCHLIST.USER_ID.eq(watcherId)).fetchOne();
        if (record != null) {
            logger.info("Found for id {}, going to delete it and its shares.", id);
            int countDelete = dsl.delete(WATCHLIST)
                    .where(WATCHLIST.ID.eq(id.intValue()))
                    .execute();
            deleted = countDelete > 0;
        } else {
            logger.warn("Did not find a watchlist with id {}, so cannot delete.", id);
        }
        return deleted;
    }

    @Override
    public WatchList getWatchListById(Long id,Watcher watcher) {
        assert watcher != null;
        assert watcher.getId() > 0;
        assert id != null;
        assert id > 0;
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(id.intValue())).fetchOne();
        return returnWatchListIfAllowed(record, watcher.getId());
    }

    @Override
    public WatchList getWatchListByName(String watchlistName, Watcher watcher) {
        assert watchlistName != null;
        assert !watchlistName.isEmpty();
        assert watcher != null;
        assert watcher.getId() > 0;
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.NAME.eq(watchlistName)).fetchOne();
        return returnWatchListIfAllowed(record, watcher.getId());
    }

    /**
     * To share a watchList to another watcher.
     *
     * @param watchList      the watchList to share
     * @param owner          the watcher that owns the watchList to share
     * @param sharer         the watcher to share the watchList with
     * @param hasWriteRights if the sharee has write rights on this watchList
     */
    @Override
    public void shareWatchList(WatchList watchList, Watcher owner, Watcher sharer, boolean hasWriteRights) {
        assert watchList != null;
        assert watchList.getId() > 0;
        assert owner != null;
        assert owner.getId() > 0;
        assert sharer != null;
        assert sharer.getId() > 0;

        Integer watchListId = ((Long)watchList.getId()).intValue();
        Record record = dsl.select().from(WATCHLIST).where(WATCHLIST.ID.eq(watchListId)).fetchOne();

        if ((record.getValue(WATCHLIST.USER_ID, Long.class) != owner.getId())) {
            logger.warn("WatchList {} share request initiated by user {}, but isn't the owner", watchListId, owner.getId());
            return;
        }

        Integer sharerId = ((Long)sharer.getId()).intValue();
        Record shareRecord = dsl.select().from(WATCHLIST_SHARES).where(WATCHLIST_SHARES.WATCHLIST_ID.eq(watchListId)).and(WATCHLIST_SHARES.WATCHER_ID.eq(sharerId)).fetchOne();

        if (shareRecord != null && shareRecord.get(WATCHLIST_SHARES.WRITE_ACCESS) == hasWriteRights) {
            logger.warn("WatchList {} was already shared with user {}", watchListId, sharerId);
        } else if (shareRecord != null) {
            int updateCount = dsl.update(WATCHLIST_SHARES)
                    .set(WATCHLIST_SHARES.WRITE_ACCESS, hasWriteRights)
                    .where(WATCHLIST_SHARES.WATCHLIST_ID.eq(watchListId))
                    .and(WATCHLIST_SHARES.WATCHER_ID.eq(sharerId))
                    .execute();

            if (updateCount > 0) {
                logger.info("WatchList {} was already shared with user {}, but write access was update to {}", watchListId, sharerId, hasWriteRights);
            }

        } else {
            dsl.insertInto(WATCHLIST_SHARES)
                    .set(WATCHLIST_SHARES.WATCHLIST_ID, watchListId)
                    .set(WATCHLIST_SHARES.WATCHER_ID, sharerId)
                    .execute();

            logger.info("WatchList {} is shared with user {}, has write access {}", watchListId, sharerId, hasWriteRights);
        }
    }

    /**
     * Return the list of watchers this watchlist is shared with.
     *
     * @param watchList the watchlist for which to check the shared with list
     * @param owner     the owner of the watchlist as check
     * @return the list of watchers to which this watchlist is shared
     */
    @Override
    public List<WatchListShare> getSharedWith(WatchList watchList, Watcher owner) {
        assert watchList != null;
        assert watchList.getId() > 0;
        assert owner != null;
        assert owner.getId() > 0;

        List<WatchListShare> response = Collections.emptyList();
        Long id = watchList.getId();
        Long ownerId = owner.getId();
        int count = dsl.selectCount()
                .from(WATCHLIST)
                .where(WATCHLIST.ID.eq(id.intValue()))
                .and(WATCHLIST.USER_ID.eq(ownerId.intValue()))
                .fetchOne(0, int.class);

        if (count < 1) { // we don't own it, we should not see the shares
            return response;
        }

        Result<Record> result = dsl.select().from(WATCHLIST_SHARES).where(WATCHLIST_SHARES.WATCHLIST_ID.eq(id.intValue())).fetch();
        if (result != null && !result.isEmpty()) {
            response = new ArrayList<>();
            for (Record record : result) {
                WatchListShare share = createWatchListShareFromRecord(record, owner);
                response.add(share);
            }
        }
        return response;
    }

    private WatchListShare createWatchListShareFromRecord(Record record, Watcher owner) {
        assert record != null;
        Long sharedWithId = record.getValue(WATCHLIST_SHARES.WATCHER_ID, Long.class);
        Long watchListId = record.getValue(WATCHLIST_SHARES.WATCHLIST_ID, Long.class);
        WatchList watchList = getWatchListById(watchListId, owner);
        Watcher sharedWith = watcherService.getWatcherById(sharedWithId);
        boolean writeAccess = record.getValue(WATCHLIST_SHARES.WRITE_ACCESS, Boolean.class);
        return new WatchListShare(watchList, sharedWith, writeAccess);
    }

    private WatchList returnWatchListIfAllowed(Record record, Long watcherId) {
        if (record == null) {
            return null;
        }

        // now check if we're allowed to see it
        if (record.getValue(WATCHLIST.USER_ID, Long.class) == watcherId) {
            return getWatchListEntity(record);
        }

        // maybe it was shared with us
        Integer watchlistId = record.getValue(WATCHLIST.ID, Integer.class);
        int count = dsl.selectCount()
                .from(WATCHLIST_SHARES)
                .where(WATCHLIST_SHARES.WATCHLIST_ID.eq(watchlistId))
                .and(WATCHLIST_SHARES.WATCHER_ID.eq(watcherId.intValue()))
                .fetchOne(0, int.class);

        if (count > 0) {
            return getWatchListEntity(record);
        }
        return null;
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
