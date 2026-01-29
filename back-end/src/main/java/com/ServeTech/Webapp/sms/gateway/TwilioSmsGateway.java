package com.ServeTech.Webapp.sms.gateway;

import com.ServeTech.Webapp.sms.config.SmsConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Twilio SMS Gateway
 * International SMS support
 * Good for global apps
 */
@Component
public class TwilioSmsGateway {

    @Autowired
    private SmsConfig smsConfig;

    /**
     * Send SMS via Twilio
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            // Initialize Twilio
            Twilio.init(smsConfig.getTwilioAccountSid(), smsConfig.getTwilioAuthToken());

            // Format phone number for India (+91)
            String formattedNumber = "+91" + phoneNumber;

            // Send SMS
            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(smsConfig.getTwilioPhoneNumber()),
                    message
            ).create();

            System.out.println("Twilio SMS sent successfully. SID: " + twilioMessage.getSid());
            return true;

        } catch (Exception e) {
            System.err.println("Twilio SMS failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
