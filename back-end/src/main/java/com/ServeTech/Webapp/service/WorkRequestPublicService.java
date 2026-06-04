package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.entity.WorkerProfile;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.WorkApplicationRepository;
import com.ServeTech.Webapp.repository.WorkRequestRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkRequestPublicService {

    private final WorkRequestRepository workRequestRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final WorkApplicationRepository workApplicationRepository;

    public WorkRequestPublicService(WorkRequestRepository workRequestRepository,
                                     WorkerProfileRepository workerProfileRepository,
                                     WorkApplicationRepository workApplicationRepository) {
        this.workRequestRepository = workRequestRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.workApplicationRepository = workApplicationRepository;
    }

    public List<WorkRequestResponse> browseJobs(Long workerId, String pincode, List<Long> skillIds, Boolean urgent) {
        // Get the worker's skills for filtering
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElse(null);
        Set<Long> workerSkillIds = new HashSet<>();
        if (profile != null && profile.getSkills() != null) {
            workerSkillIds = profile.getSkills().stream()
                    .map(s -> s.getId()).collect(Collectors.toSet());
        }

        List<WorkRequest> requests;

        if (pincode != null && !pincode.isBlank() && skillIds != null && !skillIds.isEmpty()) {
            requests = workRequestRepository.findMatchingRequestsForWorker(pincode, skillIds);
        } else if (pincode != null && !pincode.isBlank()) {
            requests = workRequestRepository.findOpenRequestsByPincode(pincode);
        } else if (urgent != null && urgent) {
            requests = workRequestRepository.findUrgentOpenRequests();
        } else {
            requests = workRequestRepository.findAllOpenRequests();
        }

        // Filter: only show jobs that match the worker's skills
        // Jobs with no required skills are shown to everyone
        final Set<Long> finalWorkerSkillIds = workerSkillIds;
        return requests.stream()
                .filter(wr -> {
                    if (wr.getRequiredSkills() == null || wr.getRequiredSkills().isEmpty()) {
                        return true; // no skill requirement, show to all
                    }
                    // Show only if worker has at least one matching skill
                    return wr.getRequiredSkills().stream()
                            .anyMatch(skill -> finalWorkerSkillIds.contains(skill.getId()));
                })
                .map(wr -> {
                    WorkRequestResponse response = WorkRequestResponse.fromEntity(wr);
                    response.setHasApplied(
                            workApplicationRepository.existsByWorkerIdAndWorkRequestId(workerId, wr.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<WorkRequestResponse> getRecommendedJobs(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new CustomException("Worker profile not found", HttpStatus.NOT_FOUND));

        String pincode = profile.getUser().getPincode();
        List<Long> skillIds = profile.getSkills().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        // Multi-tier recommendation: collect jobs from best match to broadest
        List<WorkRequest> results = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        // Tier 1: Exact pincode + matching skills (best match)
        if (pincode != null && !skillIds.isEmpty()) {
            List<WorkRequest> tier1 = workRequestRepository.findMatchingRequestsForWorker(pincode, skillIds);
            for (WorkRequest wr : tier1) {
                if (seenIds.add(wr.getId())) results.add(wr);
            }
        }

        // Tier 2: Matching skills anywhere (regardless of pincode)
        if (!skillIds.isEmpty()) {
            List<WorkRequest> tier2 = workRequestRepository.findOpenRequestsBySkills(skillIds);
            for (WorkRequest wr : tier2) {
                if (seenIds.add(wr.getId())) results.add(wr);
            }
        }

        // Tier 3: All open jobs that match worker's skills (fallback if still few results)
        if (results.size() < 5) {
            Set<Long> workerSkillIdSet = new HashSet<>(skillIds);
            List<WorkRequest> tier3 = workRequestRepository.findAllOpenRequests();
            for (WorkRequest wr : tier3) {
                if (seenIds.add(wr.getId())) {
                    // Only include if no skills required or worker has a matching skill
                    if (wr.getRequiredSkills() == null || wr.getRequiredSkills().isEmpty()) {
                        results.add(wr);
                    } else {
                        boolean matches = wr.getRequiredSkills().stream()
                                .anyMatch(s -> workerSkillIdSet.contains(s.getId()));
                        if (matches) results.add(wr);
                    }
                }
            }
        }

        return results.stream()
                .map(wr -> {
                    WorkRequestResponse response = WorkRequestResponse.fromEntity(wr);
                    response.setHasApplied(
                            workApplicationRepository.existsByWorkerIdAndWorkRequestId(workerId, wr.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public WorkRequestResponse getJobDetails(Long jobId, Long workerId) {
        WorkRequest workRequest = workRequestRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));

        WorkRequestResponse response = WorkRequestResponse.fromEntity(workRequest);
        if (workerId != null) {
            response.setHasApplied(
                    workApplicationRepository.existsByWorkerIdAndWorkRequestId(workerId, jobId));
        }
        return response;
    }
}
