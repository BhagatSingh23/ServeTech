package com.ServeTech.Webapp.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SMS Configuration properties
 * Reads values from application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {

    private String provider; // twilio, msg91, fast2sms

    // Twilio Configuration
    private String twilioAccountSid;
    private String twilioAuthToken;
    private String twilioPhoneNumber;

    // MSG91 Configuration
    private String msg91AuthKey;
    private String msg91SenderId;
    private String msg91TemplateId;
    private String msg91Route;

    // Fast2SMS Configuration
    private String fast2smsApiKey;
    private String fast2smsSenderId;

    // Common Configuration
    private boolean enabled = true; // Enable/disable SMS

    // Constructors
    public SmsConfig() {
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }

    public void setTwilioAccountSid(String twilioAccountSid) {
        this.twilioAccountSid = twilioAccountSid;
    }

    public String getTwilioAuthToken() {
        return twilioAuthToken;
    }

    public void setTwilioAuthToken(String twilioAuthToken) {
        this.twilioAuthToken = twilioAuthToken;
    }

    public String getTwilioPhoneNumber() {
        return twilioPhoneNumber;
    }

    public void setTwilioPhoneNumber(String twilioPhoneNumber) {
        this.twilioPhoneNumber = twilioPhoneNumber;
    }

    public String getMsg91AuthKey() {
        return msg91AuthKey;
    }

    public void setMsg91AuthKey(String msg91AuthKey) {
        this.msg91AuthKey = msg91AuthKey;
    }

    public String getMsg91SenderId() {
        return msg91SenderId;
    }

    public void setMsg91SenderId(String msg91SenderId) {
        this.msg91SenderId = msg91SenderId;
    }

    public String getMsg91TemplateId() {
        return msg91TemplateId;
    }

    public void setMsg91TemplateId(String msg91TemplateId) {
        this.msg91TemplateId = msg91TemplateId;
    }

    public String getMsg91Route() {
        return msg91Route;
    }

    public void setMsg91Route(String msg91Route) {
        this.msg91Route = msg91Route;
    }

    public String getFast2smsApiKey() {
        return fast2smsApiKey;
    }

    public void setFast2smsApiKey(String fast2smsApiKey) {
        this.fast2smsApiKey = fast2smsApiKey;
    }

    public String getFast2smsSenderId() {
        return fast2smsSenderId;
    }

    public void setFast2smsSenderId(String fast2smsSenderId) {
        this.fast2smsSenderId = fast2smsSenderId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
