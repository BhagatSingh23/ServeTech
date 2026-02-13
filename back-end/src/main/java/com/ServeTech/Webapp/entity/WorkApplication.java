package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Work Application entity - Tracks applications for work
@Entity
@Table(
        name = "work_applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"work_request_id", "worker_id"})
        },
        indexes = {
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_worker", columnList = "worker_id"),
                @Index(name = "idx_request", columnList = "work_request_id")
        }
)
public class WorkApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Work request this application is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_request_id", nullable = false)
    private WorkRequest workRequest;

    // Worker who applied
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Column(length = 1000, name = "cover_message")
    private String coverMessage;

    // Worker's proposed wage (can differ from client's offer)
    @Column(name = "proposed_wage_per_day")
    private Double proposedWagePerDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Client's notes when accepting/rejecting
    @Column(name = "review_notes", length = 500)
    private String reviewNotes;

    // Constructors
    public WorkApplication() {
        this.status = ApplicationStatus.PENDING;
    }

    public WorkApplication(WorkRequest workRequest, User worker) {
        this();
        this.workRequest = workRequest;
        this.worker = worker;
    }

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkRequest getWorkRequest() {
        return workRequest;
    }

    public void setWorkRequest(WorkRequest workRequest) {
        this.workRequest = workRequest;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public String getCoverMessage() {
        return coverMessage;
    }

    public void setCoverMessage(String coverMessage) {
        this.coverMessage = coverMessage;
    }

    public Double getProposedWagePerDay() {
        return proposedWagePerDay;
    }

    public void setProposedWagePerDay(Double proposedWagePerDay) {
        this.proposedWagePerDay = proposedWagePerDay;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    // Helper Methods

    // Method to accept this application
    public void accept(String notes) {
        this.status = ApplicationStatus.ACCEPTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewNotes = notes;
    }

    // Method to reject this application
    public void reject(String notes) {
        this.status = ApplicationStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewNotes = notes;
    }

    // Method to withdraw this application
    public void withdraw() {
        this.status = ApplicationStatus.WITHDRAWN;
        this.reviewedAt = LocalDateTime.now();
    }

    // Check if application is pending
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    // Custom toString method
    @Override
    public String toString() {
        return "WorkApplication{" +
                "id=" + id +
                ", status=" + status +
                ", proposedWagePerDay=" + proposedWagePerDay +
                ", appliedAt=" + appliedAt +
                '}';
    }
}
