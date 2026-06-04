package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Location;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PincodeApiResponse {

    @JsonAlias({"Message"})
    private String message;

    @JsonAlias({"Status"})
    private String status;

    @JsonProperty("PostOffice")
    private List<Location> postOffice;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Location> getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(List<Location> postOffice) {
        this.postOffice = postOffice;
    }
}
