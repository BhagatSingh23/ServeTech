package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Complaint;
import com.ServeTech.Webapp.entity.enums.ComplaintPriority;
import com.ServeTech.Webapp.entity.enums.ComplaintStatus;
import com.ServeTech.Webapp.entity.enums.ComplaintType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ComplaintResponse {

    private Long id;
    private String complaintId;
    private String complainantName;
    private Long complainantId;
    private String accusedName;
    private Long accusedId;
    private Long assignmentId;
    private ComplaintType category;
    private String subject;
    private String description;
    private List<String> evidenceUrls;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private String assignedAdminName;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public ComplaintResponse() {
    }

    public static ComplaintResponse fromEntity(Complaint c) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(c.getId());
        response.setComplaintId(c.getComplaintId());
        response.setComplainantName(c.getComplainant().getFullName());
        response.setComplainantId(c.getComplainant().getId());
        response.setAccusedName(c.getAccused().getFullName());
        response.setAccusedId(c.getAccused().getId());

        if (c.getAssignment() != null) {
            response.setAssignmentId(c.getAssignment().getId());
        }

        response.setCategory(c.getComplaintType());
        response.setSubject(c.getSubject());
        response.setDescription(c.getDescription());

        if (c.getEvidenceUrls() != null && !c.getEvidenceUrls().isEmpty()) {
            response.setEvidenceUrls(Arrays.asList(c.getEvidenceUrls().split(",")));
        } else {
            response.setEvidenceUrls(Collections.emptyList());
        }

        response.setStatus(c.getStatus());
        response.setPriority(c.getPriority());

        if (c.getAssignedAdmin() != null) {
            response.setAssignedAdminName(c.getAssignedAdmin().getFullName());
        }

        response.setResolution(c.getResolutionDetails());
        response.setCreatedAt(c.getFiledAt());
        response.setResolvedAt(c.getResolvedAt());

        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }
    public String getComplainantName() { return complainantName; }
    public void setComplainantName(String complainantName) { this.complainantName = complainantName; }
    public Long getComplainantId() { return complainantId; }
    public void setComplainantId(Long complainantId) { this.complainantId = complainantId; }
    public String getAccusedName() { return accusedName; }
    public void setAccusedName(String accusedName) { this.accusedName = accusedName; }
    public Long getAccusedId() { return accusedId; }
    public void setAccusedId(Long accusedId) { this.accusedId = accusedId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public ComplaintType getCategory() { return category; }
    public void setCategory(ComplaintType category) { this.category = category; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getEvidenceUrls() { return evidenceUrls; }
    public void setEvidenceUrls(List<String> evidenceUrls) { this.evidenceUrls = evidenceUrls; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public ComplaintPriority getPriority() { return priority; }
    public void setPriority(ComplaintPriority priority) { this.priority = priority; }
    public String getAssignedAdminName() { return assignedAdminName; }
    public void setAssignedAdminName(String assignedAdminName) { this.assignedAdminName = assignedAdminName; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
