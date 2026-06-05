package com.ServeTech.Webapp.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @JsonProperty("block")
    @JsonAlias({"Block", "Name"})
    private String block;

    @JsonProperty("district")
    @JsonAlias({"District"})
    private String district;

    @JsonProperty("state")
    @JsonAlias({"State"})
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
