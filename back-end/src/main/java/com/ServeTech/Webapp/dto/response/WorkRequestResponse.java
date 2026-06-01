package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class WorkRequestResponse {

    private Long id;
    private String requestId;
    private String clientName;
    private Long clientId;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String pincode;
    private String workAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer estimatedDurationDays;
    private Integer workersNeeded;
    private Double offeredWagePerDay;
    private Double totalBudget;
    private WorkRequestStatus status;
    private Boolean isUrgent;
    private String additionalRequirements;
    private LocalDateTime createdAt;
    private Boolean hasApplied;

    public WorkRequestResponse() {
    }

    public static WorkRequestResponse fromEntity(WorkRequest wr) {
        WorkRequestResponse response = new WorkRequestResponse();
        response.setId(wr.getId());
        response.setRequestId(wr.getRequestId());
        response.setClientName(wr.getClient().getFullName());
        response.setClientId(wr.getClient().getId());
        response.setTitle(wr.getTitle());
        response.setDescription(wr.getDescription());
        response.setPincode(wr.getPincode());
        response.setWorkAddress(wr.getWorkAddress());
        response.setStartDate(wr.getStartDate());
        response.setEndDate(wr.getEndDate());
        response.setEstimatedDurationDays(wr.getEstimatedDurationDays());
        response.setWorkersNeeded(wr.getWorkersNeeded());
        response.setOfferedWagePerDay(wr.getOfferedWagePerDay());
        response.setTotalBudget(wr.getTotalBudget());
        response.setStatus(wr.getStatus());
        response.setIsUrgent(wr.getIsUrgent());
        response.setCreatedAt(wr.getCreatedAt());
        response.setHasApplied(false);

        if (wr.getRequiredSkills() != null) {
            response.setRequiredSkills(wr.getRequiredSkills().stream()
                    .map(skill -> skill.getName().name())
                    .collect(Collectors.toList()));
        }

        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
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
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }
    public WorkRequestStatus getStatus() { return status; }
    public void setStatus(WorkRequestStatus status) { this.status = status; }
    public Boolean getIsUrgent() { return isUrgent; }
    public void setIsUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; }
    public String getAdditionalRequirements() { return additionalRequirements; }
    public void setAdditionalRequirements(String additionalRequirements) { this.additionalRequirements = additionalRequirements; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getHasApplied() { return hasApplied; }
    public void setHasApplied(Boolean hasApplied) { this.hasApplied = hasApplied; }
}
