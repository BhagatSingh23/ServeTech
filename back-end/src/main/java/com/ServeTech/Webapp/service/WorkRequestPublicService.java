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

import java.util.List;
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

    public List<WorkRequestResponse> browseJobs(String pincode, List<Long> skillIds, Boolean urgent) {
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

        return requests.stream()
                .map(WorkRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WorkRequestResponse> getRecommendedJobs(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new CustomException("Worker profile not found", HttpStatus.NOT_FOUND));

        String pincode = profile.getUser().getPincode();
        List<Long> skillIds = profile.getSkills().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        List<WorkRequest> requests;
        if (pincode != null && !skillIds.isEmpty()) {
            requests = workRequestRepository.findMatchingRequestsForWorker(pincode, skillIds);
        } else if (pincode != null) {
            requests = workRequestRepository.findOpenRequestsByPincode(pincode);
        } else {
            requests = workRequestRepository.findAllOpenRequests();
        }

        return requests.stream()
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
