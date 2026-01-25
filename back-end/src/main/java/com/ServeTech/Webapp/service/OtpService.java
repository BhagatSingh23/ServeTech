package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.entity.OtpVerification;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;


// OtpService class to handle OTP verification and generation
@Service
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    private static final SecureRandom random = new SecureRandom();

    // Generate and send OTP to user's phone number
    // We still need to integrate SMS gateway to send OTP to user's phone number
    @Transactional
    public String generateAndSendOtp(String phoneNumber, String purpose) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        // Create OTP verification entry
        OtpVerification otpVerification = new OtpVerification(phoneNumber, otp, purpose);
        otpRepository.save(otpVerification);

        // TODO: Integrate SMS gateway to send OTP
        // Example: smsGateway.sendSms(phoneNumber, "Your OTP is: " + otp);
        System.out.println("OTP for " + phoneNumber + ": " + otp); // For testing

        return otp; // Remove this in production
    }

    // Verify OTP and mark it as used
    @Transactional
    public boolean verifyOtp(String phoneNumber, String otp, String purpose) {
        Optional<OtpVerification> otpOptional = otpRepository
                .findTopByPhoneNumberAndPurposeOrderByCreatedAtDesc(phoneNumber, purpose);

        if (otpOptional.isEmpty()) {
            throw new CustomException("No OTP found for this phone number");
        }

        OtpVerification otpVerification = otpOptional.get();

        if (otpVerification.isExpired()) {
            throw new CustomException("OTP has expired");
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

    // Clean up expired OTPs
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}
