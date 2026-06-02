package com.ServeTech.Webapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @com.fasterxml.jackson.annotation.JsonAlias("Block")
    @JsonProperty("block")
    private String block;

    @com.fasterxml.jackson.annotation.JsonAlias("District")
    @JsonProperty("district")
    private String district;

    @com.fasterxml.jackson.annotation.JsonAlias("State")
    @JsonProperty("state")
    private String state;

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
