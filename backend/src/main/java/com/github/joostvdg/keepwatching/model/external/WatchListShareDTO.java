package com.github.joostvdg.keepwatching.model.external;

public class WatchListShareDTO {

    private String sharerIdentifier;
    private Long watchListId;
    private boolean hasWriteAccess;

    public WatchListShareDTO() {
        super();
    }

    public String getSharerIdentifier() {
        return sharerIdentifier;
    }

    public void setSharerIdentifier(String sharerIdentifier) {
        this.sharerIdentifier = sharerIdentifier;
    }

    public Long getWatchListId() {
        return watchListId;
    }

    public void setWatchListId(Long watchListId) {
        this.watchListId = watchListId;
    }

    public boolean isHasWriteAccess() {
        return hasWriteAccess;
    }

    public void setHasWriteAccess(boolean hasWriteAccess) {
        this.hasWriteAccess = hasWriteAccess;
    }
}
