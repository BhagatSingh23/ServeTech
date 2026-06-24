package com.ServeTech.Webapp.config;

import com.ServeTech.Webapp.service.OtpService;
import com.ServeTech.Webapp.repository.WorkRequestRepository;
import com.ServeTech.Webapp.repository.WorkApplicationRepository;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.WorkApplication;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import com.ServeTech.Webapp.entity.enums.ApplicationStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private final OtpService otpService;
    private final WorkRequestRepository workRequestRepository;
    private final WorkApplicationRepository workApplicationRepository;

    public ScheduledTasks(OtpService otpService, WorkRequestRepository workRequestRepository, WorkApplicationRepository workApplicationRepository) {
        this.otpService = otpService;
        this.workRequestRepository = workRequestRepository;
        this.workApplicationRepository = workApplicationRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredOtps() {
        System.out.println("Running scheduled OTP cleanup...");
        otpService.cleanupExpiredOtps();
        System.out.println("OTP cleanup completed");
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredJobs() {
        System.out.println("Running scheduled job closure...");
        List<WorkRequest> expiredJobs = workRequestRepository.findByStatusAndEndDateBefore(WorkRequestStatus.OPEN, LocalDateTime.now());
        for (WorkRequest job : expiredJobs) {
            job.setStatus(WorkRequestStatus.CLOSED);
            job.setClosedAt(LocalDateTime.now());
            workRequestRepository.save(job);
            
            List<WorkApplication> pendingApps = workApplicationRepository.findByWorkRequestIdAndStatusOrderByAppliedAtDesc(job.getId(), ApplicationStatus.PENDING);
            workApplicationRepository.deleteAll(pendingApps);
        }
        System.out.println("Closed " + expiredJobs.size() + " expired jobs.");
    }
}