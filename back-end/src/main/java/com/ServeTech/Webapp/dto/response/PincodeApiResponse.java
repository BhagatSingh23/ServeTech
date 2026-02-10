package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Location;

import java.util.List;

public class PincodeApiResponse {

    private String Message;
    private String Status;
    private List<Location> location;

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

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }
}
