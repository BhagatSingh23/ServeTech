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
    private final EmailService emailService;

    public OtpService(OtpVerificationRepository otpRepository, SmsService smsService, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    private static final SecureRandom random = new SecureRandom();

    // Generate and send otp via sms and/or email
    @Transactional
    public String generateAndSendOtp(String phoneNumber, String email, String purpose) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            OtpVerification phoneVerification = new OtpVerification(phoneNumber, otp, purpose);
            otpRepository.save(phoneVerification);
            boolean smsSent = smsService.sendOtpSms(phoneNumber, otp);
            if (!smsSent) {
                System.err.println("Failed to send SMS to " + phoneNumber);
            }
            System.out.println("OTP for " + phoneNumber + ": " + otp);
        }

        if (email != null && !email.trim().isEmpty()) {
            OtpVerification emailVerification = new OtpVerification(null, email, otp, purpose);
            otpRepository.save(emailVerification);
            try {
                emailService.sendOtpEmail(email, otp);
            } catch (Exception e) {
                System.err.println("Failed to send Email to " + email);
            }
            System.out.println("OTP for " + email + ": " + otp);
        }

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
            if (!otpVerification.getOtp().equals(otp)) {
                throw new CustomException("Invalid OTP");
            }
            return true;
        }

        boolean isValid = otpVerification.verify(otp);
        if (isValid) {
            otpRepository.save(otpVerification);
            return true;
        }

        throw new CustomException("Invalid OTP");
    }

    @Transactional
    public boolean verifyEmailOtp(String email, String otp, String purpose) {
        Optional<OtpVerification> otpOptional = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);

        if (otpOptional.isEmpty()) {
            throw new CustomException("No OTP found for this email");
        }

        OtpVerification otpVerification = otpOptional.get();

        if (otpVerification.isExpired()) {
            throw new CustomException("OTP has expired. Please request a new OTP");
        }

        if (otpVerification.getIsVerified()) {
            if (!otpVerification.getOtp().equals(otp)) {
                throw new CustomException("Invalid OTP");
            }
            return true;
        }

        boolean isValid = otpVerification.verify(otp);
        if (isValid) {
            otpRepository.save(otpVerification);
            return true;
        }

        throw new CustomException("Invalid OTP");
    }

    // Check if OTP was already successfully verified for this phone number and purpose
    public boolean isOtpAlreadyVerified(String phoneNumber, String purpose) {
        Optional<OtpVerification> otpOptional = otpRepository
                .findTopByPhoneNumberAndPurposeOrderByCreatedAtDesc(phoneNumber, purpose);

        return otpOptional.isPresent() 
                && otpOptional.get().getIsVerified() 
                && !otpOptional.get().isExpired();
    }

    // Check if OTP was already successfully verified for this email and purpose
    public boolean isEmailOtpAlreadyVerified(String email, String purpose) {
        Optional<OtpVerification> otpOptional = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);

        return otpOptional.isPresent() 
                && otpOptional.get().getIsVerified() 
                && !otpOptional.get().isExpired();
    }

    // Remove the expired OTPs from the db
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}
