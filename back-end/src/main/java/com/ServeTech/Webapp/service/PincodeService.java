package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.PincodeApiResponse;
import com.ServeTech.Webapp.entity.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PincodeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Location fetchLocation(String pincode) {

        String url = "https://api.postalpincode.in/pincode/" + pincode;

        ResponseEntity<PincodeApiResponse[]> response =
                restTemplate.getForEntity(url, PincodeApiResponse[].class);

        PincodeApiResponse[] body = response.getBody();

        if (body == null || body.length == 0) {
            throw new RuntimeException("No Location found for the given Pincode from API");
        }

        // First object
        PincodeApiResponse apiResponse = body[0];

        if (!apiResponse.getStatus().equalsIgnoreCase("Success")) {
            throw new RuntimeException("Invalid Pincode");
        }

        // First Location pops-up choose that
        Location location = apiResponse.getLocation().getFirst();

        return location;
    }
}
