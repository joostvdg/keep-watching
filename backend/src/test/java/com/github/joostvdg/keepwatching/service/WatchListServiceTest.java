package com.github.joostvdg.keepwatching.service;

import com.github.joostvdg.keepwatching.model.WatchList;
import com.github.joostvdg.keepwatching.model.WatchListShare;
import com.github.joostvdg.keepwatching.model.Watcher;
//import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.*;

//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class WatchListServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private WatchListService watchListService;

    @Autowired
    private WatcherService watcherService;

    private String uniqueName = "test";;
    private WatchList watchList;

    private Watcher watcher;
    String watcherIdentifier = "12345";

    private Watcher watcher2;
    String watcher2Identifier = "67890";

    @BeforeEach
    public void setup() {
        watcher = watcherService.getWatcherByIdentifier(watcherIdentifier);
        if (watcher == null) {
            watcher = new Watcher();
            watcher.setName("Pietje");
            watcher.setIdentifier(watcherIdentifier);
            watcher = watcherService.newWatcher(watcher);
        }
        watcher2 = watcherService.getWatcherByIdentifier(watcher2Identifier);
        if (watcher2 == null) {
            watcher2 = new Watcher();
            watcher2.setName("Hans");
            watcher2.setIdentifier(watcher2Identifier);
            watcher2 = watcherService.newWatcher(watcher2);
        }
    }

    @Test
    public void shouldCreateAndDeleteWatchList() {
        watchList = new WatchList(uniqueName, watcher);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        Assertions.assertNotNull(created);
        Assertions.assertTrue(created.getId() > 0);
        Assertions.assertEquals(uniqueName, created.getName());
        boolean isDeleted = watchListService.deleteWatchListById(created.getId(), watcher);
        Assertions.assertTrue(isDeleted);
    }

    @Test
    public void cannotCreateTwoWatchListWithTheSameName() throws DataIntegrityViolationException {
        watchList = new WatchList(uniqueName, watcher);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        Assertions.assertNotNull(created);
        Assertions.assertEquals(uniqueName, created.getName());
        watchListService.newWatchList(watchList, watcher);
    }

    @Test
    public void canOnlyRetrieveOwnedAndSharedWatchLists(){
        watchList = new WatchList(uniqueName, watcher);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        Assertions.assertNotNull(created);

        List<WatchList> watchListsFound = watchListService.getAllWatchLists(watcher);
        Assertions.assertNotNull(watchListsFound);
        Assertions.assertFalse(watchListsFound.isEmpty());

        watchListsFound = watchListService.getAllWatchLists(watcher2);
        Assertions.assertNotNull(watchListsFound);
        Assertions.assertTrue(watchListsFound.isEmpty());

        watchListService.shareWatchList(watchList, watcher, watcher2, false);

        watchListsFound = watchListService.getAllWatchLists(watcher2);
        Assertions.assertNotNull(watchListsFound);
        Assertions.assertEquals(1, watchListsFound.size());
    }

    @Test
    public void getListOfWatchersSharingTheWatchList() {
        watchList = new WatchList(uniqueName, watcher);
        WatchList created = watchListService.newWatchList(watchList, watcher);
        Assertions.assertNotNull(created);

        List<WatchListShare> sharedWith = watchListService.getSharedWith(created, watcher);
        Assertions.assertNotNull(sharedWith);
        Assertions.assertTrue(sharedWith.isEmpty());

        watchListService.shareWatchList(created, watcher, watcher2, false);

        sharedWith = watchListService.getSharedWith(created, watcher);
        Assertions.assertNotNull(sharedWith);
        Assertions.assertEquals(1, sharedWith.size());

    }
}
