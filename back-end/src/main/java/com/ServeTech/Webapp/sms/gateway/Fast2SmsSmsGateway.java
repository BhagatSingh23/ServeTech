package com.ServeTech.Webapp.sms.gateway;

import com.ServeTech.Webapp.sms.config.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Fast2SMS Gateway
 * Indian SMS provider
 * Easy to setup
 * Good for testing and small scale
 */
@Component
public class Fast2SmsSmsGateway {

    @Autowired
    private SmsConfig smsConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send SMS via Fast2SMS
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            // Build URL with query parameters manually
            String url = String.format(
                    "https://www.fast2sms.com/dev/bulkV2?authorization=%s&route=dlt&sender_id=%s&message=%s&variables_values=%s&flash=0&numbers=%s",
                    smsConfig.getFast2smsApiKey(),
                    smsConfig.getFast2smsSenderId(),
                    message,
                    message,
                    phoneNumber
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", smsConfig.getFast2smsApiKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Fast2SMS Response: " + response.getBody());
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("Fast2SMS failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
