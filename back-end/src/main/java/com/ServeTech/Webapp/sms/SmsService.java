package com.ServeTech.Webapp.sms;

// SMS Service interface
// All SMS related operations should be defined here
public interface SmsService {

    // Send SMS True if send successfully
    boolean sendSms(String phoneNumber, String message);

    // Send OTP SMS
    boolean sendOtpSms(String phoneNumber, String otp);
}