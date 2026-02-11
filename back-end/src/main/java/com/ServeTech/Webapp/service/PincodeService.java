package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.PincodeApiResponse;
import com.ServeTech.Webapp.entity.Location;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class PincodeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Location fetchLocation(String pincode) {

        String url = "https://api.postalpincode.in/pincode/" + pincode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PincodeApiResponse[]> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        PincodeApiResponse[].class
                );

        PincodeApiResponse[] body = response.getBody();

        if (body == null || body.length == 0) {
            throw new RuntimeException("No response from API");
        }

        PincodeApiResponse apiResponse = body[0];

        if (!"Success".equalsIgnoreCase(apiResponse.getStatus())) {
            throw new RuntimeException("Invalid Pincode");
        }

        return apiResponse.getPostOffice().get(0);
    }
}
