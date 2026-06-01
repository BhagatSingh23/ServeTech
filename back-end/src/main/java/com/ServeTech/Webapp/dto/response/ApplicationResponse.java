package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.WorkApplication;
import com.ServeTech.Webapp.entity.enums.ApplicationStatus;

import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private Long workRequestId;
    private String workRequestTitle;
    private String workerName;
    private Long workerId;
    private Double workerRating;
    private Integer workerExperience;
    private Double proposedWagePerDay;
    private String coverLetter;
    private Integer availableDays;
    private ApplicationStatus status;
    private String reviewNotes;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;

    public ApplicationResponse() {
    }

    public static ApplicationResponse fromEntity(WorkApplication app) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(app.getId());
        response.setWorkRequestId(app.getWorkRequest().getId());
        response.setWorkRequestTitle(app.getWorkRequest().getTitle());
        response.setWorkerName(app.getWorker().getFullName());
        response.setWorkerId(app.getWorker().getId());
        response.setProposedWagePerDay(app.getProposedWagePerDay());
        response.setCoverLetter(app.getCoverMessage());
        response.setStatus(app.getStatus());
        response.setReviewNotes(app.getReviewNotes());
        response.setAppliedAt(app.getAppliedAt());
        response.setReviewedAt(app.getReviewedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    public String getWorkRequestTitle() { return workRequestTitle; }
    public void setWorkRequestTitle(String workRequestTitle) { this.workRequestTitle = workRequestTitle; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public Double getWorkerRating() { return workerRating; }
    public void setWorkerRating(Double workerRating) { this.workerRating = workerRating; }
    public Integer getWorkerExperience() { return workerExperience; }
    public void setWorkerExperience(Integer workerExperience) { this.workerExperience = workerExperience; }
    public Double getProposedWagePerDay() { return proposedWagePerDay; }
    public void setProposedWagePerDay(Double proposedWagePerDay) { this.proposedWagePerDay = proposedWagePerDay; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public Integer getAvailableDays() { return availableDays; }
    public void setAvailableDays(Integer availableDays) { this.availableDays = availableDays; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
