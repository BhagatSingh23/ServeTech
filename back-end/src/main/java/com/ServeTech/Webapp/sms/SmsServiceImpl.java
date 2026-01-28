package com.ServeTech.Webapp.sms;

import com.ServeTech.Webapp.sms.config.SmsConfig;
import com.ServeTech.Webapp.sms.gateway.Fast2SmsSmsGateway;
import com.ServeTech.Webapp.sms.gateway.Msg91SmsGateway;
import com.ServeTech.Webapp.sms.gateway.TwilioSmsGateway;
import org.springframework.stereotype.Service;

/**
 * SMS Service Implementation
 * Handles SMS sending with multiple gateway support
 */
@Service
public class SmsServiceImpl implements SmsService {

    private final SmsConfig smsConfig;

    private final TwilioSmsGateway twilioGateway;

    private final Msg91SmsGateway msg91Gateway;

    private final Fast2SmsSmsGateway fast2smsGateway;


    public SmsServiceImpl(SmsConfig smsConfig, TwilioSmsGateway twilioGateway,
                          Msg91SmsGateway msg91Gateway, Fast2SmsSmsGateway fast2smsGateway) {
        this.smsConfig = smsConfig;
        this.twilioGateway = twilioGateway;
        this.msg91Gateway = msg91Gateway;
        this.fast2smsGateway = fast2smsGateway;
    }

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        if (!smsConfig.isEnabled()) {
            System.out.println("SMS disabled. Would send: " + message + " to " + phoneNumber);
            return true; // For testing
        }

        // Choose gateway based on configuration
        String provider = smsConfig.getProvider();

        try {
            return switch (provider.toLowerCase()) {
                case "twilio" -> twilioGateway.sendSms(phoneNumber, message);
                case "msg91" -> msg91Gateway.sendSms(phoneNumber, message);
                case "fast2sms" -> fast2smsGateway.sendSms(phoneNumber, message);
                default -> {
                    System.err.println("Unknown SMS provider: " + provider);
                    yield false;
                }
            };
        } catch (Exception e) {
            System.err.println("SMS sending failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendOtpSms(String phoneNumber, String otp) {
        String message = String.format(
                "Your OTP for ServeTech registration is %s. Valid for 10 minutes. Do not share this code with anyone.",
                otp
        );

        return sendSms(phoneNumber, message);
    }
}