package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.CreateApplicationRequest;
import com.ServeTech.Webapp.dto.response.ApplicationResponse;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkApplication;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.WorkerProfile;
import com.ServeTech.Webapp.entity.enums.ApplicationStatus;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.*;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkApplicationService {

    private final WorkApplicationRepository workApplicationRepository;
    private final WorkRequestRepository workRequestRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final UniqueIdGenerator uniqueIdGenerator;

    public WorkApplicationService(WorkApplicationRepository workApplicationRepository,
                                  WorkRequestRepository workRequestRepository,
                                  WorkAssignmentRepository workAssignmentRepository,
                                  UserRepository userRepository,
                                  WorkerProfileRepository workerProfileRepository,
                                  ClientProfileRepository clientProfileRepository,
                                  UniqueIdGenerator uniqueIdGenerator) {
        this.workApplicationRepository = workApplicationRepository;
        this.workRequestRepository = workRequestRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @Transactional
    public ApplicationResponse applyForJob(Long workerId, CreateApplicationRequest request) {
        WorkRequest workRequest = workRequestRepository.findById(request.getWorkRequestId())
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (workRequest.getStatus() != WorkRequestStatus.OPEN) {
            throw new CustomException("This job is no longer accepting applications", HttpStatus.BAD_REQUEST);
        }

        if (workApplicationRepository.existsByWorkerIdAndWorkRequestId(workerId, request.getWorkRequestId())) {
            throw new CustomException("You have already applied to this job", HttpStatus.CONFLICT);
        }

        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new CustomException("Worker not found", HttpStatus.NOT_FOUND));

        // Skill validation: worker must have at least one skill matching the job's required skills
        if (workRequest.getRequiredSkills() != null && !workRequest.getRequiredSkills().isEmpty()) {
            WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                    .orElseThrow(() -> new CustomException("Worker profile not found. Please complete your profile first.", HttpStatus.NOT_FOUND));

            Set<Long> workerSkillIds = profile.getSkills().stream()
                    .map(s -> s.getId()).collect(Collectors.toSet());
            Set<Long> requiredSkillIds = workRequest.getRequiredSkills().stream()
                    .map(s -> s.getId()).collect(Collectors.toSet());

            boolean hasMatchingSkill = workerSkillIds.stream().anyMatch(requiredSkillIds::contains);
            if (!hasMatchingSkill) {
                String required = workRequest.getRequiredSkills().stream()
                        .map(s -> s.getName().name())
                        .collect(Collectors.joining(", "));
                throw new CustomException(
                        "You don't have the required skills for this job. Required: " + required,
                        HttpStatus.BAD_REQUEST);
            }
        }

        WorkApplication application = new WorkApplication();
        application.setWorker(worker);
        application.setWorkRequest(workRequest);
        application.setProposedWagePerDay(request.getProposedWagePerDay());
        application.setCoverMessage(request.getCoverLetter());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        WorkApplication saved = workApplicationRepository.save(application);
        return ApplicationResponse.fromEntity(saved);
    }

    public List<ApplicationResponse> getMyApplications(Long workerId, String status) {
        List<WorkApplication> applications;
        if (status != null && !status.isBlank()) {
            try {
                ApplicationStatus as = ApplicationStatus.valueOf(status.toUpperCase());
                applications = workApplicationRepository.findByWorkerIdAndStatusOrderByAppliedAtDesc(workerId, as);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
            }
        } else {
            applications = workApplicationRepository.findByWorkerIdOrderByAppliedAtDesc(workerId);
        }
        return applications.stream().map(ApplicationResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse withdrawApplication(Long workerId, Long applicationId) {
        WorkApplication application = workApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getWorker().getId().equals(workerId)) {
            throw new CustomException("Access denied: not your application", HttpStatus.FORBIDDEN);
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new CustomException("Can only withdraw pending applications", HttpStatus.BAD_REQUEST);
        }

        application.withdraw();
        WorkApplication saved = workApplicationRepository.save(application);
        return ApplicationResponse.fromEntity(saved);
    }

    public List<ApplicationResponse> getApplicationsForWorkRequest(Long clientId, Long workRequestId) {
        WorkRequest workRequest = workRequestRepository.findById(workRequestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        return workApplicationRepository.findByWorkRequestIdOrderByAppliedAtDesc(workRequestId)
                .stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse acceptApplication(Long clientId, Long applicationId) {
        WorkApplication application = workApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));

        WorkRequest workRequest = application.getWorkRequest();

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new CustomException("Can only accept pending applications", HttpStatus.BAD_REQUEST);
        }

        // Accept the application
        application.accept("Accepted by client");
        workApplicationRepository.save(application);

        // Create a work assignment
        WorkAssignment assignment = new WorkAssignment();
        assignment.setAssignmentId(uniqueIdGenerator.generateWorkAssignmentId());
        assignment.setWorkRequest(workRequest);
        assignment.setWorker(application.getWorker());
        assignment.setClient(workRequest.getClient());
        assignment.setAgreedWagePerDay(application.getProposedWagePerDay() != null
                ? application.getProposedWagePerDay()
                : workRequest.getOfferedWagePerDay());
        assignment.setStartDate(workRequest.getStartDate() != null ? workRequest.getStartDate().toLocalDate() : null);
        assignment.setExpectedEndDate(workRequest.getEndDate() != null ? workRequest.getEndDate().toLocalDate() : null);
        assignment.setProgressStatus(WorkProgressStatus.NOT_STARTED);

        // Calculate total amount
        int days = workRequest.getEstimatedDurationDays() != null ? workRequest.getEstimatedDurationDays() : 1;
        assignment.setTotalAmount(assignment.getAgreedWagePerDay() * days);
        assignment.setAmountPending(assignment.getTotalAmount());

        workAssignmentRepository.save(assignment);

        // Update work request status if all workers hired
        long acceptedCount = workApplicationRepository.countByWorkRequestIdAndStatus(
                workRequest.getId(), ApplicationStatus.ACCEPTED);
        if (acceptedCount >= workRequest.getWorkersNeeded()) {
            workRequest.setStatus(WorkRequestStatus.IN_PROGRESS);
            workRequestRepository.save(workRequest);
        }

        // Update worker profile stats
        workerProfileRepository.findByUserId(application.getWorker().getId()).ifPresent(profile -> {
            profile.incrementInProgressJobs();
            workerProfileRepository.save(profile);
        });

        // Update client profile stats
        clientProfileRepository.findByUserId(clientId).ifPresent(profile -> {
            profile.incrementWorkersHired();
            clientProfileRepository.save(profile);
        });

        return ApplicationResponse.fromEntity(application);
    }

    @Transactional
    public ApplicationResponse rejectApplication(Long clientId, Long applicationId, String reason) {
        WorkApplication application = workApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));

        WorkRequest workRequest = application.getWorkRequest();

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new CustomException("Can only reject pending applications", HttpStatus.BAD_REQUEST);
        }

        application.reject(reason);
        WorkApplication saved = workApplicationRepository.save(application);
        return ApplicationResponse.fromEntity(saved);
    }
}
