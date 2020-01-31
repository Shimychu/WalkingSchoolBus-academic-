package com.group.peach.thewalkingschoolbus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnedRewards {


    private int tier = 0;

    // Needed for JSON deserialization
    public EarnedRewards() {
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
