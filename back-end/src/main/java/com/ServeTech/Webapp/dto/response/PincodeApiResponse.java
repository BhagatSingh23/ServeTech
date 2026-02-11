package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Location;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PincodeApiResponse {

    private String Message;
    private String Status;

    @JsonProperty("PostOffice")
    private List<Location> postOffice;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<Location> getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(List<Location> postOffice) {
        this.postOffice = postOffice;
    }
}
