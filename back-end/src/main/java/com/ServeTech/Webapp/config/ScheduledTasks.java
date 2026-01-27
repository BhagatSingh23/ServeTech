package com.ServeTech.Webapp.config;

import com.ServeTech.Webapp.service.OtpService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Class to configure scheduled tasks
@Component
public class ScheduledTasks {

    private final OtpService otpService;

    public ScheduledTasks(OtpService otpService) {
        this.otpService = otpService;
    }

    // Clean up expired OTPs every day at midnight
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredOtps() {
        System.out.println("Running scheduled OTP cleanup...");
        otpService.cleanupExpiredOtps();
        System.out.println("OTP cleanup completed");
    }
}