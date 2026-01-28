package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.entity.OtpVerification;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.OtpVerificationRepository;
import com.ServeTech.Webapp.sms.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;

    private final SmsService smsService;

    public OtpService(OtpVerificationRepository otpRepository, SmsService smsService) {
        this.otpRepository = otpRepository;
        this.smsService = smsService;
    }

    private static final SecureRandom random = new SecureRandom();

    // Generate and send otp via sms
    @Transactional
    public String generateAndSendOtp(String phoneNumber, String purpose) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        // Create OTP verification entry
        OtpVerification otpVerification = new OtpVerification(phoneNumber, otp, purpose);
        otpRepository.save(otpVerification);

        // Send OTP via SMS
        boolean smsSent = smsService.sendOtpSms(phoneNumber, otp);

        if (!smsSent) {
            System.err.println("Failed to send SMS to " + phoneNumber);
            // Still return OTP for testing even if SMS fails
        }

        System.out.println("OTP for " + phoneNumber + ": " + otp); // For testing

        return otp;
    }

    // Verify the otp
    @Transactional
    public boolean verifyOtp(String phoneNumber, String otp, String purpose) {
        Optional<OtpVerification> otpOptional = otpRepository
                .findTopByPhoneNumberAndPurposeOrderByCreatedAtDesc(phoneNumber, purpose);

        if (otpOptional.isEmpty()) {
            throw new CustomException("No OTP found for this phone number");
        }

        OtpVerification otpVerification = otpOptional.get();

        if (otpVerification.isExpired()) {
            throw new CustomException("OTP has expired. Please request a new OTP");
        }

        if (otpVerification.getIsVerified()) {
            throw new CustomException("OTP already used");
        }

        boolean isValid = otpVerification.verify(otp);
        if (isValid) {
            otpRepository.save(otpVerification);
            return true;
        }

        throw new CustomException("Invalid OTP");
    }

    // Remove the expired OTPs from the db
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}
