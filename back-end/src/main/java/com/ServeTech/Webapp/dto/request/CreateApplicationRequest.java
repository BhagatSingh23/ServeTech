package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.*;

public class CreateApplicationRequest {

    @NotNull(message = "Work request ID is required")
    private Long workRequestId;

    @NotNull(message = "Proposed wage per day is required")
    @DecimalMin(value = "1", message = "Proposed wage must be at least 1")
    private Double proposedWagePerDay;

    @Size(max = 1000, message = "Cover letter must not exceed 1000 characters")
    private String coverLetter;

    private Integer availableDays;

    public CreateApplicationRequest() {
    }

    // Getters and Setters
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    public Double getProposedWagePerDay() { return proposedWagePerDay; }
    public void setProposedWagePerDay(Double proposedWagePerDay) { this.proposedWagePerDay = proposedWagePerDay; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public Integer getAvailableDays() { return availableDays; }
    public void setAvailableDays(Integer availableDays) { this.availableDays = availableDays; }
}
