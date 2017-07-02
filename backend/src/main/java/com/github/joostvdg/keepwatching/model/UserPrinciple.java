package com.github.joostvdg.keepwatching.model;

/**
 * Created by joost on 18-6-17.
 */
public class UserPrinciple {

    private final String name;
    private final String principle;

    public UserPrinciple(String name, String principle) {
        this.name = name;
        this.principle = principle;
    }

    public String getName() {
        return name;
    }

    public String getPrinciple() {
        return principle;
    }
}
