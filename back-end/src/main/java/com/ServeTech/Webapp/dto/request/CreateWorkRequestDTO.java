package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class CreateWorkRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotEmpty(message = "At least one skill is required")
    private List<Long> skillIds;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
    private String pincode;

    private String workAddress;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Integer estimatedDurationDays;

    @Min(value = 1, message = "At least 1 worker is needed")
    private Integer workersNeeded;

    @NotNull(message = "Offered wage per day is required")
    @DecimalMin(value = "1", message = "Offered wage must be at least 1")
    private Double offeredWagePerDay;

    private Boolean isUrgent;

    private String additionalRequirements;

    public CreateWorkRequestDTO() {
    }

    // Calculate total budget: offeredWagePerDay * estimatedDurationDays * workersNeeded
    public Double calculateTotalBudget() {
        if (offeredWagePerDay != null && estimatedDurationDays != null && workersNeeded != null) {
            return offeredWagePerDay * estimatedDurationDays * workersNeeded;
        }
        if (offeredWagePerDay != null && estimatedDurationDays != null) {
            return offeredWagePerDay * estimatedDurationDays;
        }
        return null;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(List<Long> skillIds) { this.skillIds = skillIds; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getWorkAddress() { return workAddress; }
    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getEstimatedDurationDays() { return estimatedDurationDays; }
    public void setEstimatedDurationDays(Integer estimatedDurationDays) { this.estimatedDurationDays = estimatedDurationDays; }
    public Integer getWorkersNeeded() { return workersNeeded; }
    public void setWorkersNeeded(Integer workersNeeded) { this.workersNeeded = workersNeeded; }
    public Double getOfferedWagePerDay() { return offeredWagePerDay; }
    public void setOfferedWagePerDay(Double offeredWagePerDay) { this.offeredWagePerDay = offeredWagePerDay; }
    public Boolean getIsUrgent() { return isUrgent; }
    public void setIsUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; }
    public String getAdditionalRequirements() { return additionalRequirements; }
    public void setAdditionalRequirements(String additionalRequirements) { this.additionalRequirements = additionalRequirements; }
}
