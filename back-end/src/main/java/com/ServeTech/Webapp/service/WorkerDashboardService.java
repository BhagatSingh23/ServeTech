package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.WorkerDashboardDTO;
import com.ServeTech.Webapp.dto.response.WorkerDashboardSummaryDTO;
import com.ServeTech.Webapp.entity.*;
import com.ServeTech.Webapp.entity.enums.SkillType;
import com.ServeTech.Webapp.repository.WorkAssignmentRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkerDashboardService {


    // Constructor Injection
    private final WorkAssignmentRepository workAssignmentRepository;

    private final WorkerProfileRepository workerProfileRepository;

    public WorkerDashboardService(WorkAssignmentRepository workAssignmentRepository, WorkerProfileRepository workerProfileRepository) {
        this.workAssignmentRepository = workAssignmentRepository;
        this.workerProfileRepository = workerProfileRepository;
    }

    // Get worker dashboard summary data
    public WorkerDashboardSummaryDTO getWorkerDashboard(Long workerId) {
        WorkerDashboardSummaryDTO summary = new WorkerDashboardSummaryDTO();

        // Get worker profile for stats
        WorkerProfile workerProfile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // Set summary statistics from profile
        summary.setTotalEarnings(workerProfile.getTotalEarnings());
        summary.setTotalJobsCompleted(workerProfile.getTotalJobsCompleted());
        summary.setTotalJobsInProgress(workerProfile.getTotalJobsInProgress());
        summary.setAverageRating(workerProfile.getAverageRating());
        summary.setTotalRatings(workerProfile.getTotalRatings());
        summary.setAvailableForWork(workerProfile.getAvailableForWork());

        // Calculate pending payments
        Double pendingPayments = workAssignmentRepository.calculatePendingPaymentsByWorkerId(workerId);
        summary.setPendingPayments(pendingPayments != null ? pendingPayments : 0.0);

        // Get bookings categorized by status
        List<WorkerDashboardDTO> previousBookings = getCompletedBookings(workerId);
        List<WorkerDashboardDTO> currentBookings = getCurrentBookings(workerId);
        List<WorkerDashboardDTO> upcomingBookings = getUpcomingBookings(workerId);

        summary.setPreviousBookings(previousBookings);
        summary.setCurrentBookings(currentBookings);
        summary.setUpcomingBookings(upcomingBookings);
        summary.setActiveAssignments(currentBookings.size() + upcomingBookings.size());

        return summary;
    }

    // Get all completed bookings (Previous Bookings section)
    public List<WorkerDashboardDTO> getCompletedBookings(Long workerId) {
        List<WorkAssignment> completedAssignments =
                workAssignmentRepository.findCompletedAssignmentsByWorkerId(workerId);

        return completedAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Current bookings (In-Progress section)
    public List<WorkerDashboardDTO> getCurrentBookings(Long workerId) {
        List<WorkAssignment> inProgressAssignments =
                workAssignmentRepository.findInProgressAssignmentsByWorkerId(workerId);

        return inProgressAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get upcoming bookings (not yet started)
    public List<WorkerDashboardDTO> getUpcomingBookings(Long workerId) {
        List<WorkAssignment> upcomingAssignments =
                workAssignmentRepository.findUpcomingAssignmentsByWorkerId(workerId);

        return upcomingAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all the bookings for a worker
    public List<WorkerDashboardDTO> getAllBookings(Long workerId) {
        List<WorkAssignment> allAssignments =
                workAssignmentRepository.findAllByWorkerId(workerId);

        return allAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert WorkAssignment entity to WorkerDashboardDTO
    private WorkerDashboardDTO convertToDTO(WorkAssignment assignment) {
        WorkerDashboardDTO dto = new WorkerDashboardDTO();

        // Assignment info
        dto.setAssignmentId(assignment.getId());
        dto.setAssignmentNumber(assignment.getAssignmentId());

        // Client/Employer info
        User client = assignment.getClient();
        dto.setEmployerName(client.getFullName());
        dto.setEmployerPhone(client.getPhoneNumber());
        dto.setEmployerLocation(client.getDistrict() + ", " + client.getState());

        // Work details from WorkRequest
        WorkRequest workRequest = assignment.getWorkRequest();
        dto.setWorkTitle(workRequest.getTitle());
        dto.setWorkDescription(workRequest.getDescription());

        // Get primary skill as "Role"
        String role = String.valueOf(workRequest.getRequiredSkills().stream()
                .findFirst()
                .map(Skill::getName)
                .orElse(SkillType.LABOUR));
        dto.setRole(role);

        // Dates
        dto.setStartDate(assignment.getStartDate());
        dto.setExpectedEndDate(assignment.getExpectedEndDate());
        dto.setActualEndDate(assignment.getActualEndDate());
        dto.setDaysWorked(assignment.getTotalDaysWorked());

        // Location
        dto.setWorkLocation(workRequest.getWorkAddress() != null ?
                workRequest.getWorkAddress() : client.getDistrict());
        dto.setPincode(workRequest.getPincode());

        // Payment - FIXED to match your entity structure
        dto.setAgreedWagePerDay(assignment.getAgreedWagePerDay());
        dto.setTotalAmount(assignment.getTotalAmount());

        // Handle amountPaid - might be null
        Double amountPaid = assignment.getAmountPaid();
        dto.setAmountPaid(amountPaid != null ? amountPaid : 0.0);

        // Handle amountPending - might be null
        Double amountPending = assignment.getAmountPending();
        dto.setAmountPending(amountPending != null ? amountPending : 0.0);

        dto.setPaymentStatus(assignment.getPaymentStatus());

        // Progress
        dto.setProgressStatus(assignment.getProgressStatus());
        dto.setProgressPercentage(assignment.getProgressPercentage());

        // Rating
        dto.setClientRating(assignment.getClientRating());
        dto.setClientReview(assignment.getClientReview());

        // Timestamps
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setCompletedAt(assignment.getCompletedAt());

        return dto;
    }

    // Update worker stats when assignment is completed
    public void updateWorkerStatsOnCompletion(Long workerId, WorkAssignment assignment) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // Increment completed jobs
        profile.incrementCompletedJobs();

        // Decrement in-progress jobs
        profile.decrementInProgressJobs();

        // Add earnings if paid - FIXED to handle null
        Double amountPaid = assignment.getAmountPaid();
        if (amountPaid != null && amountPaid > 0) {
            profile.addEarnings(amountPaid);
        }

        // Update rating if client has rated
        if (assignment.getClientRating() != null) {
            profile.updateRating(assignment.getClientRating());
        }

        workerProfileRepository.save(profile);
    }

    // Update worker stats when assignment starts
    public void updateWorkerStatsOnStart(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        profile.incrementInProgressJobs();
        workerProfileRepository.save(profile);
    }
}