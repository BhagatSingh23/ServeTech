package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// OTP Verification entity - Stores OTPs for users to verify their accounts
@Entity
@Table(
        name = "otp_verifications",
        indexes = {
                @Index(name = "idx_phone", columnList = "phone_number"),
                @Index(name = "idx_expiry", columnList = "expiry_time")
        }
)
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String otp;

    // Will set the time limit for OTP verification to 10 minutes
    @Column(nullable = false, name = "expiry_time")
    private LocalDateTime expiryTime;

    // REGISTRATION, LOGIN, PASSWORD_RESET
    @Column(nullable = false, length = 30)
    private String purpose;

    @Column(nullable = false, name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // OTP valid for 10 minutes
        expiryTime = createdAt.plusMinutes(10);
    }

    // Constructors
    public OtpVerification() {
        this.isVerified = false;
    }

    public OtpVerification(String phoneNumber, String otp, String purpose) {
        this();
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.purpose = purpose;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper Methods

    // To check if OTP has expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    // Method to verify OTP
    public boolean verify(String inputOtp) {
        if (isExpired()) {
            return false;
        }
        if (this.otp.equals(inputOtp)) {
            this.isVerified = true;
            this.verifiedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // Custom toString method
    @Override
    public String toString() {
        return "OtpVerification{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", purpose='" + purpose + '\'' +
                ", isVerified=" + isVerified +
                ", expiryTime=" + expiryTime +
                '}';
    }
}