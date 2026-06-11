package com.ServeTech.Webapp.dto.request;

public class FileComplaintRequest {
    private Long accusedId;
    private Long assignmentId; // optional
    private String complaintType; // maps to ComplaintType enum
    private String subject;
    private String description;
    private String priority; // optional, defaults to MEDIUM

    // Getters and Setters
    public Long getAccusedId() { return accusedId; }
    public void setAccusedId(Long accusedId) { this.accusedId = accusedId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
