package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.AdminDashboardResponse;
import com.ServeTech.Webapp.dto.response.ComplaintResponse;
import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.entity.Complaint;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.WorkerProfile;
import com.ServeTech.Webapp.entity.enums.*;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final WorkRequestRepository workRequestRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final ComplaintRepository complaintRepository;

    public AdminService(UserRepository userRepository,
                        WorkerProfileRepository workerProfileRepository,
                        WorkRequestRepository workRequestRepository,
                        WorkAssignmentRepository workAssignmentRepository,
                        ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.workRequestRepository = workRequestRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.complaintRepository = complaintRepository;
    }

    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalUsers(userRepository.count());
        response.setTotalWorkers(userRepository.countByRoleName(RoleType.ROLE_WORKER));
        response.setTotalClients(userRepository.countByRoleName(RoleType.ROLE_CLIENT));
        response.setTotalWorkRequests(workRequestRepository.count());
        response.setOpenWorkRequests(workRequestRepository.countByStatus(WorkRequestStatus.OPEN));
        response.setTotalAssignments(workAssignmentRepository.count());
        response.setPendingComplaints(complaintRepository.countByStatus(ComplaintStatus.SUBMITTED));

        List<WorkerProfile> pendingVerifications = workerProfileRepository.findByIsVerifiedFalse();
        response.setPendingVerifications((long) pendingVerifications.size());

        // Calculate total revenue from completed assignments
        List<WorkAssignment> completedAssignments = workAssignmentRepository.findByProgressStatus(WorkProgressStatus.COMPLETED);
        double totalRevenue = completedAssignments.stream()
                .mapToDouble(a -> a.getAmountPaid() != null ? a.getAmountPaid() : 0)
                .sum();
        response.setTotalRevenue(totalRevenue);

        // Pending verifications list
        List<AdminDashboardResponse.WorkerVerificationDTO> verificationList = pendingVerifications.stream()
                .limit(10)
                .map(wp -> {
                    AdminDashboardResponse.WorkerVerificationDTO dto = new AdminDashboardResponse.WorkerVerificationDTO();
                    dto.setUserId(wp.getUser().getId());
                    dto.setName(wp.getUser().getFirstName() + " " + wp.getUser().getLastName());
                    dto.setPhoneNumber(wp.getUser().getPhoneNumber());
                    dto.setPincode(wp.getUser().getPincode());
                    dto.setSkills(wp.getSkills().stream()
                            .map(s -> s.getName().name())
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        response.setPendingVerificationsList(verificationList);

        return response;
    }

    public List<com.ServeTech.Webapp.dto.response.UserResponse> getAllUsers(String role, String status) {
        List<User> users;

        if (role != null && !role.isBlank() && status != null && !status.isBlank()) {
            try {
                String parsedRole = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
                RoleType roleType = RoleType.valueOf(parsedRole);
                AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
                users = userRepository.findByRoleNameAndAccountStatus(roleType, accountStatus);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid role or status filter", HttpStatus.BAD_REQUEST);
            }
        } else if (role != null && !role.isBlank()) {
            try {
                String parsedRole = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
                RoleType roleType = RoleType.valueOf(parsedRole);
                users = userRepository.findByRoleName(roleType);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid role: " + role, HttpStatus.BAD_REQUEST);
            }
        } else if (status != null && !status.isBlank()) {
            try {
                AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
                users = userRepository.findByAccountStatus(accountStatus);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
            }
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(com.ServeTech.Webapp.dto.response.UserResponse::new)
                .collect(Collectors.toList());
    }

    public com.ServeTech.Webapp.dto.response.UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return new com.ServeTech.Webapp.dto.response.UserResponse(user);
    }

    @Transactional
    public com.ServeTech.Webapp.dto.response.UserResponse updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        try {
            AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase());
            user.setAccountStatus(newStatus);
            User saved = userRepository.save(user);
            return new com.ServeTech.Webapp.dto.response.UserResponse(saved);
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid account status: " + status, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void verifyWorker(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new CustomException("Worker profile not found", HttpStatus.NOT_FOUND));
        profile.setIsVerified(true);
        workerProfileRepository.save(profile);
    }

    @Transactional
    public void rejectVerification(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new CustomException("Worker profile not found", HttpStatus.NOT_FOUND));
        workerProfileRepository.delete(profile);
        userRepository.deleteById(workerId);
    }

    public List<WorkRequestResponse> getAllWorkRequests(String status) {
        List<WorkRequest> requests;
        if (status != null && !status.isBlank()) {
            try {
                WorkRequestStatus ws = WorkRequestStatus.valueOf(status.toUpperCase());
                requests = workRequestRepository.findByStatusOrderByCreatedAtDesc(ws);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
            }
        } else {
            requests = workRequestRepository.findAll();
        }
        return requests.stream().map(WorkRequestResponse::fromEntity).collect(Collectors.toList());
    }

    public List<ComplaintResponse> getAllComplaints(String status) {
        List<Complaint> complaints;
        if (status != null && !status.isBlank()) {
            try {
                ComplaintStatus cs = ComplaintStatus.valueOf(status.toUpperCase());
                complaints = complaintRepository.findByStatusOrderByFiledAtDesc(cs);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
            }
        } else {
            complaints = complaintRepository.findAllByOrderByFiledAtDesc();
        }
        return complaints.stream().map(ComplaintResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public ComplaintResponse assignComplaint(Long complaintId, Long adminId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new CustomException("Complaint not found", HttpStatus.NOT_FOUND));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CustomException("Admin not found", HttpStatus.NOT_FOUND));

        complaint.setAssignedAdmin(admin);
        complaint.setStatus(ComplaintStatus.UNDER_REVIEW);
        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    @Transactional
    public ComplaintResponse resolveComplaint(Long complaintId, String resolution) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new CustomException("Complaint not found", HttpStatus.NOT_FOUND));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolutionDetails(resolution);
        complaint.setResolvedAt(LocalDateTime.now());
        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }
}
