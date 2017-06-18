package com.github.joostvdg.keepwatching.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by joost on 5-6-17.
 */
public class WatchList {

    private long id;
    private String name;
    private Watcher owner;

    @Override
    public String toString() {
        return "WatchList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WatchList watchList = (WatchList) o;

        return new EqualsBuilder()
                .append(getName(), watchList.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .toHashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Watcher getOwner() {
        return owner;
    }

    public void setOwner(Watcher owner) {
        this.owner = owner;
    }
}
