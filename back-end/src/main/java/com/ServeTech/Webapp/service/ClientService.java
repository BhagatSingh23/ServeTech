package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.CreateWorkRequestDTO;
import com.ServeTech.Webapp.dto.request.UpdateWorkRequestDTO;
import com.ServeTech.Webapp.dto.response.ApplicationResponse;
import com.ServeTech.Webapp.dto.response.ClientDashboardResponse;
import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.*;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final WorkRequestRepository workRequestRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final UniqueIdGenerator uniqueIdGenerator;

    public ClientService(WorkRequestRepository workRequestRepository,
                         WorkApplicationRepository workApplicationRepository,
                         WorkAssignmentRepository workAssignmentRepository,
                         UserRepository userRepository,
                         SkillRepository skillRepository,
                         ClientProfileRepository clientProfileRepository,
                         UniqueIdGenerator uniqueIdGenerator) {
        this.workRequestRepository = workRequestRepository;
        this.workApplicationRepository = workApplicationRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    public ClientDashboardResponse getDashboard(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new CustomException("Client not found", HttpStatus.NOT_FOUND));

        long totalProjects = workRequestRepository.findByClientIdOrderByCreatedAtDesc(clientId).size();
        long activeProjects = workRequestRepository.findByClientIdAndStatusOrderByCreatedAtDesc(clientId, WorkRequestStatus.OPEN).size()
                + workRequestRepository.findByClientIdAndStatusOrderByCreatedAtDesc(clientId, WorkRequestStatus.IN_PROGRESS).size();

        List<WorkAssignment> allAssignments = workAssignmentRepository.findAllByClientId(clientId);
        long workersHired = allAssignments.size();
        double totalSpent = allAssignments.stream()
                .mapToDouble(a -> a.getAmountPaid() != null ? a.getAmountPaid() : 0)
                .sum();

        List<WorkRequestResponse> activeJobs = workRequestRepository
                .findByClientIdAndStatusOrderByCreatedAtDesc(clientId, WorkRequestStatus.OPEN)
                .stream()
                .map(WorkRequestResponse::fromEntity)
                .collect(Collectors.toList());

        activeJobs.addAll(workRequestRepository
                .findByClientIdAndStatusOrderByCreatedAtDesc(clientId, WorkRequestStatus.IN_PROGRESS)
                .stream()
                .map(WorkRequestResponse::fromEntity)
                .collect(Collectors.toList()));

        List<ClientDashboardResponse.RecentWorkerDTO> recentWorkers = allAssignments.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(a -> {
                    ClientDashboardResponse.RecentWorkerDTO dto = new ClientDashboardResponse.RecentWorkerDTO();
                    dto.setWorkerName(a.getWorker().getFirstName() + " " + a.getWorker().getLastName());
                    dto.setAssignmentId(a.getAssignmentId());
                    dto.setStatus(a.getProgressStatus().name());
                    dto.setPaymentStatus(a.getPaymentStatus().name());
                    dto.setAgreedWage(a.getAgreedWagePerDay());
                    return dto;
                })
                .collect(Collectors.toList());

        ClientDashboardResponse.DashboardStats stats = new ClientDashboardResponse.DashboardStats();
        stats.setProjectsPosted(totalProjects);
        stats.setActiveProjects(activeProjects);
        stats.setWorkersHired(workersHired);
        stats.setTotalSpent(totalSpent);

        ClientDashboardResponse response = new ClientDashboardResponse();
        response.setStats(stats);
        response.setActiveJobs(activeJobs);
        response.setRecentWorkers(recentWorkers);
        return response;
    }

    @Transactional
    public WorkRequestResponse createWorkRequest(Long clientId, CreateWorkRequestDTO request) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new CustomException("Client not found", HttpStatus.NOT_FOUND));

        WorkRequest workRequest = new WorkRequest();
        workRequest.setRequestId(uniqueIdGenerator.generateWorkRequestId());
        workRequest.setClient(client);
        workRequest.setTitle(request.getTitle());
        workRequest.setDescription(request.getDescription());
        workRequest.setPincode(request.getPincode());
        workRequest.setWorkAddress(request.getWorkAddress());
        workRequest.setStartDate(request.getStartDate());
        workRequest.setEndDate(request.getEndDate());
        workRequest.setWorkersNeeded(request.getWorkersNeeded());
        workRequest.setOfferedWagePerDay(request.getOfferedWagePerDay());
        workRequest.setIsUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false);
        workRequest.setStatus(WorkRequestStatus.OPEN);

        if (request.getEstimatedDurationDays() != null) {
            workRequest.setEstimatedDurationDays(request.getEstimatedDurationDays());
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            workRequest.setEstimatedDurationDays((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()));
        }

        // Calculate total budget
        int days = workRequest.getEstimatedDurationDays() != null ? workRequest.getEstimatedDurationDays() : 1;
        int workers = workRequest.getWorkersNeeded() != null ? workRequest.getWorkersNeeded() : 1;
        workRequest.setTotalBudget(request.getOfferedWagePerDay() * days * workers);

        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            workRequest.setRequiredSkills(skills);
        }

        WorkRequest saved = workRequestRepository.save(workRequest);

        // Update client profile stats
        clientProfileRepository.findByUserId(clientId).ifPresent(profile -> {
            profile.incrementWorkRequestsPosted();
            clientProfileRepository.save(profile);
        });

        return WorkRequestResponse.fromEntity(saved);
    }

    public List<WorkRequestResponse> getWorkRequests(Long clientId, String status) {
        List<WorkRequest> requests;
        if (status != null && !status.isBlank()) {
            try {
                WorkRequestStatus ws = WorkRequestStatus.valueOf(status.toUpperCase());
                requests = workRequestRepository.findByClientIdAndStatusOrderByCreatedAtDesc(clientId, ws);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
            }
        } else {
            requests = workRequestRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        }
        return requests.stream().map(WorkRequestResponse::fromEntity).collect(Collectors.toList());
    }

    public WorkRequestResponse getWorkRequestById(Long clientId, Long requestId) {
        WorkRequest request = workRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!request.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        return WorkRequestResponse.fromEntity(request);
    }

    @Transactional
    public WorkRequestResponse updateWorkRequest(Long clientId, Long requestId, UpdateWorkRequestDTO request) {
        WorkRequest workRequest = workRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        if (workRequest.getStatus() != WorkRequestStatus.DRAFT && workRequest.getStatus() != WorkRequestStatus.OPEN) {
            throw new CustomException("Cannot update work request in " + workRequest.getStatus() + " status", HttpStatus.BAD_REQUEST);
        }

        if (request.getTitle() != null) workRequest.setTitle(request.getTitle());
        if (request.getDescription() != null) workRequest.setDescription(request.getDescription());
        if (request.getPincode() != null) workRequest.setPincode(request.getPincode());
        if (request.getWorkAddress() != null) workRequest.setWorkAddress(request.getWorkAddress());
        if (request.getStartDate() != null) workRequest.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) workRequest.setEndDate(request.getEndDate());
        if (request.getWorkersNeeded() != null) workRequest.setWorkersNeeded(request.getWorkersNeeded());
        if (request.getOfferedWagePerDay() != null) workRequest.setOfferedWagePerDay(request.getOfferedWagePerDay());
        if (request.getIsUrgent() != null) workRequest.setIsUrgent(request.getIsUrgent());
        if (request.getEstimatedDurationDays() != null) workRequest.setEstimatedDurationDays(request.getEstimatedDurationDays());

        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            workRequest.setRequiredSkills(skills);
        }

        // Recalculate budget
        int days = workRequest.getEstimatedDurationDays() != null ? workRequest.getEstimatedDurationDays() : 1;
        int workers = workRequest.getWorkersNeeded() != null ? workRequest.getWorkersNeeded() : 1;
        double wage = workRequest.getOfferedWagePerDay() != null ? workRequest.getOfferedWagePerDay() : 0;
        workRequest.setTotalBudget(wage * days * workers);

        WorkRequest saved = workRequestRepository.save(workRequest);
        return WorkRequestResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteWorkRequest(Long clientId, Long requestId) {
        WorkRequest workRequest = workRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        if (workRequest.getStatus() != WorkRequestStatus.DRAFT && workRequest.getStatus() != WorkRequestStatus.OPEN) {
            throw new CustomException("Cannot delete work request in " + workRequest.getStatus() + " status", HttpStatus.BAD_REQUEST);
        }

        workRequestRepository.delete(workRequest);
    }

    @Transactional
    public WorkRequestResponse closeWorkRequest(Long clientId, Long requestId) {
        WorkRequest workRequest = workRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        workRequest.setStatus(WorkRequestStatus.CLOSED);
        workRequest.setClosedAt(LocalDateTime.now());
        WorkRequest saved = workRequestRepository.save(workRequest);
        return WorkRequestResponse.fromEntity(saved);
    }

    public List<ApplicationResponse> getApplicationsForWorkRequest(Long clientId, Long requestId) {
        WorkRequest workRequest = workRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Work request not found", HttpStatus.NOT_FOUND));

        if (!workRequest.getClient().getId().equals(clientId)) {
            throw new CustomException("Access denied: not your work request", HttpStatus.FORBIDDEN);
        }

        return workApplicationRepository.findByWorkRequestIdOrderByAppliedAtDesc(requestId)
                .stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
