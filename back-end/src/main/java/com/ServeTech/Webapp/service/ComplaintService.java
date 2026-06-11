package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.FileComplaintRequest;
import com.ServeTech.Webapp.dto.response.ComplaintResponse;
import com.ServeTech.Webapp.entity.Complaint;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.enums.ComplaintPriority;
import com.ServeTech.Webapp.entity.enums.ComplaintType;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.ComplaintRepository;
import com.ServeTech.Webapp.repository.UserRepository;
import com.ServeTech.Webapp.repository.WorkAssignmentRepository;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final UniqueIdGenerator uniqueIdGenerator;

    public ComplaintService(ComplaintRepository complaintRepository,
                           UserRepository userRepository,
                           WorkAssignmentRepository workAssignmentRepository,
                           UniqueIdGenerator uniqueIdGenerator) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @Transactional
    public ComplaintResponse fileComplaint(Long userId, FileComplaintRequest request) {
        User complainant = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        User accused = userRepository.findById(request.getAccusedId())
                .orElseThrow(() -> new CustomException("Accused user not found", HttpStatus.NOT_FOUND));

        ComplaintType complaintType;
        try {
            complaintType = ComplaintType.valueOf(request.getComplaintType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid complaint type: " + request.getComplaintType(), HttpStatus.BAD_REQUEST);
        }

        Complaint complaint = new Complaint(complainant, accused, complaintType,
                request.getSubject(), request.getDescription());
        complaint.setComplaintId(uniqueIdGenerator.generateComplaintId());

        if (request.getAssignmentId() != null) {
            WorkAssignment assignment = workAssignmentRepository.findById(request.getAssignmentId())
                    .orElseThrow(() -> new CustomException("Work assignment not found", HttpStatus.NOT_FOUND));
            complaint.setAssignment(assignment);
        }

        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            try {
                complaint.setPriority(ComplaintPriority.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // keep default MEDIUM
            }
        }

        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    public List<ComplaintResponse> getMyComplaints(Long userId) {
        return complaintRepository.findByComplainantIdOrderByFiledAtDesc(userId)
                .stream()
                .map(ComplaintResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
