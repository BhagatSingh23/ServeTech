package com.ServeTech.Webapp.sms.gateway;

import com.ServeTech.Webapp.sms.config.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * MSG91 SMS Gateway
 * Popular in India
 * Cost-effective for Indian numbers
 * Supports DLT templates
 */
@Component
public class Msg91SmsGateway {

    @Autowired
    private SmsConfig smsConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send SMS via MSG91
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            String url = "https://api.msg91.com/api/v5/flow/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", smsConfig.getMsg91AuthKey());

            // Build request body
            String requestBody = String.format(
                    "{" +
                            "\"template_id\": \"%s\"," +
                            "\"short_url\": \"0\"," +
                            "\"recipients\": [{" +
                            "\"mobiles\": \"91%s\"," +
                            "\"OTP\": \"%s\"" +
                            "}]" +
                            "}",
                    smsConfig.getMsg91TemplateId(),
                    phoneNumber,
                    message
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("MSG91 Response: " + response.getBody());
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("MSG91 SMS failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send OTP via MSG91 (OTP-specific API)
     */
    public boolean sendOtp(String phoneNumber, String otp) {
        try {
            // Build URL with query parameters manually
            String url = String.format(
                    "https://api.msg91.com/api/v5/otp?template_id=%s&mobile=91%s&authkey=%s&otp=%s",
                    smsConfig.getMsg91TemplateId(),
                    phoneNumber,
                    smsConfig.getMsg91AuthKey(),
                    otp
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            System.out.println("MSG91 OTP Response: " + response.getBody());
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("MSG91 OTP failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}