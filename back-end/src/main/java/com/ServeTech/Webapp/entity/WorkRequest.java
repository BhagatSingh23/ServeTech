package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// This db table stores work requests for clients
// This will be used by clients to post work requests
@Entity
@Table(
        name = "work_requests",
        indexes = {
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_pincode", columnList = "pincode"),
                @Index(name = "idx_client", columnList = "client_id")
        }
)
public class WorkRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique Request ID
    @Column(nullable = false, unique = true, length = 20, name = "request_id")
    private String requestId;

    // Client who posted this work request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    // Required skills for this job (Many-to-Many)
    // Creates join table: work_request_skills
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "work_request_skills",
            joinColumns = @JoinColumn(name = "work_request_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

    // ---------- Location ----------
    @Column(nullable = false, length = 6)
    private String pincode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private PincodeLocation location;

    @Column(length = 500, name = "work_address")
    private String workAddress;

    // ---------- Work Details ----------
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    @Column(name = "workers_needed", nullable = false)
    private Integer workersNeeded = 1;

    @Column(name = "offered_wage_per_day")
    private Double offeredWagePerDay;

    @Column(name = "total_budget")
    private Double totalBudget;

    // ---------- Status ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkRequestStatus status = WorkRequestStatus.OPEN;

    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent = false;

    // ---------- Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public WorkRequest() {
        this.requiredSkills = new HashSet<>();
        this.status = WorkRequestStatus.OPEN;
        this.isUrgent = false;
        this.workersNeeded = 1;
    }

    public WorkRequest(User client, String title, String description) {
        this();
        this.client = client;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public PincodeLocation getLocation() {
        return location;
    }

    public void setLocation(PincodeLocation location) {
        this.location = location;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getEstimatedDurationDays() {
        return estimatedDurationDays;
    }

    public void setEstimatedDurationDays(Integer estimatedDurationDays) {
        this.estimatedDurationDays = estimatedDurationDays;
    }

    public Integer getWorkersNeeded() {
        return workersNeeded;
    }

    public void setWorkersNeeded(Integer workersNeeded) {
        this.workersNeeded = workersNeeded;
    }

    public Double getOfferedWagePerDay() {
        return offeredWagePerDay;
    }

    public void setOfferedWagePerDay(Double offeredWagePerDay) {
        this.offeredWagePerDay = offeredWagePerDay;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public WorkRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WorkRequestStatus status) {
        this.status = status;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    // Helper Methods

    // To add a required skill to this work request
    public void addRequiredSkill(Skill skill) {
        this.requiredSkills.add(skill);
    }

    // Remove a required skill from this work request
    public void removeRequiredSkill(Skill skill) {
        this.requiredSkills.remove(skill);
    }

    // Check if this work request is accepting applications
    public boolean isAcceptingApplications() {
        return this.status == WorkRequestStatus.OPEN;
    }

    // Mark this work request as closed
    public void closeRequest() {
        this.status = WorkRequestStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }


    // Custom toString method
    @Override
    public String toString() {
        return "WorkRequest{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", workersNeeded=" + workersNeeded +
                ", offeredWagePerDay=" + offeredWagePerDay +
                '}';
    }
}