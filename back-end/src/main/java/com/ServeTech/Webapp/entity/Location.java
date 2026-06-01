package com.ServeTech.Webapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @JsonProperty("block")
    private String Block;
    @JsonProperty("district")
    private String District;
    @JsonProperty("state")
    private String State;

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
