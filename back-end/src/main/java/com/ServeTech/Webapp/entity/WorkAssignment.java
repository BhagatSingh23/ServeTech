package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// This db table stores work assignments for workers
@Entity
@Table(
        name = "work_assignments",
        indexes = {
                @Index(name = "idx_worker", columnList = "worker_id"),
                @Index(name = "idx_client", columnList = "client_id"),
                @Index(name = "idx_status", columnList = "progress_status")
        }
)
public class WorkAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique Assignment ID (format: WA2026000001, WA2026000002, etc.)
    @Column(nullable = false, unique = true, length = 20, name = "assignment_id")
    private String assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_request_id", nullable = false)
    private WorkRequest workRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // ---------- Work Details ----------
    @Column(name = "agreed_wage_per_day", nullable = false)
    private Double agreedWagePerDay;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "total_days_worked")
    private Integer totalDaysWorked = 0;

    // ---------- Progress Tracking ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "progress_status")
    private WorkProgressStatus progressStatus = WorkProgressStatus.NOT_STARTED;

    // Progress percentage (0-100)
    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Column(length = 2000, name = "progress_notes")
    private String progressNotes;

    // ---------- Payment ----------
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid = 0.0;

    @Column(name = "amount_pending", nullable = false)
    private Double amountPending = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // ---------- Ratings (filled after completion) ----------
    // Client rates worker (1.0 to 5.0)
    @Column(name = "client_rating")
    private Double clientRating;

    @Column(name = "client_review", length = 1000)
    private String clientReview;

    // Worker rates client (1.0 to 5.0)
    @Column(name = "worker_rating")
    private Double workerRating;

    @Column(name = "worker_review", length = 1000)
    private String workerReview;

    // ---------- Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructors
    public WorkAssignment() {
        this.progressStatus = WorkProgressStatus.NOT_STARTED;
        this.progressPercentage = 0;
        this.totalDaysWorked = 0;
        this.totalAmount = 0.0;
        this.amountPaid = 0.0;
        this.amountPending = 0.0;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public WorkAssignment(WorkRequest workRequest, User worker, User client, Double agreedWagePerDay) {
        this();
        this.workRequest = workRequest;
        this.worker = worker;
        this.client = client;
        this.agreedWagePerDay = agreedWagePerDay;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        amountPending = totalAmount - amountPaid;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        amountPending = totalAmount - amountPaid;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
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

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Double getAgreedWagePerDay() {
        return agreedWagePerDay;
    }

    public void setAgreedWagePerDay(Double agreedWagePerDay) {
        this.agreedWagePerDay = agreedWagePerDay;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Integer getTotalDaysWorked() {
        return totalDaysWorked;
    }

    public void setTotalDaysWorked(Integer totalDaysWorked) {
        this.totalDaysWorked = totalDaysWorked;
    }

    public WorkProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(WorkProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getProgressNotes() {
        return progressNotes;
    }

    public void setProgressNotes(String progressNotes) {
        this.progressNotes = progressNotes;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAmountPending() {
        return amountPending;
    }

    public void setAmountPending(Double amountPending) {
        this.amountPending = amountPending;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getClientRating() {
        return clientRating;
    }

    public void setClientRating(Double clientRating) {
        this.clientRating = clientRating;
    }

    public String getClientReview() {
        return clientReview;
    }

    public void setClientReview(String clientReview) {
        this.clientReview = clientReview;
    }

    public Double getWorkerRating() {
        return workerRating;
    }

    public void setWorkerRating(Double workerRating) {
        this.workerRating = workerRating;
    }

    public String getWorkerReview() {
        return workerReview;
    }

    public void setWorkerReview(String workerReview) {
        this.workerReview = workerReview;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    // Helper Methods

    // Method to update progress of work assignment
    public void updateProgress(Integer percentage, String notes) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        this.progressPercentage = percentage;
        this.progressNotes = notes;

        if (percentage == 100) {
            this.progressStatus = WorkProgressStatus.COMPLETED;
            this.actualEndDate = LocalDate.now();
            this.completedAt = LocalDateTime.now();
        } else if (percentage > 0 && this.progressStatus == WorkProgressStatus.NOT_STARTED) {
            this.progressStatus = WorkProgressStatus.IN_PROGRESS;
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
        }
    }

    // Method to start work
    public void startWork() {
        this.progressStatus = WorkProgressStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    // Mark work as completed
    public void completeWork() {
        this.progressStatus = WorkProgressStatus.COMPLETED;
        this.progressPercentage = 100;
        this.actualEndDate = LocalDate.now();
        this.completedAt = LocalDateTime.now();
    }

    // Put work on hold
    public void putOnHold() {
        this.progressStatus = WorkProgressStatus.ON_HOLD;
    }

    // Cancel work
    public void cancelWork() {
        this.progressStatus = WorkProgressStatus.CANCELLED;
    }

    // Add payment to assignment
    public void addPayment(Double amount) {
        if (amount != null && amount > 0) {
            this.amountPaid += amount;
            this.amountPending = this.totalAmount - this.amountPaid;

            if (this.amountPaid >= this.totalAmount) {
                this.paymentStatus = PaymentStatus.PAID;
            } else if (this.amountPaid > 0) {
                this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
            }
        }
    }

    // Calculate total amount based on agreed wage and total days worked
    public void calculateTotalAmount() {
        if (this.totalDaysWorked != null && this.agreedWagePerDay != null) {
            this.totalAmount = this.totalDaysWorked * this.agreedWagePerDay;
            this.amountPending = this.totalAmount - this.amountPaid;
        }
    }

    // -------- Rating methods ------------
    public void rateWorker(Double rating, String review) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        this.clientRating = rating;
        this.clientReview = review;
    }

    public void rateClient(Double rating, String review) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        this.workerRating = rating;
        this.workerReview = review;
    }

    // Custom toString method
    @Override
    public String toString() {
        return "WorkAssignment{" +
                "id=" + id +
                ", assignmentId='" + assignmentId + '\'' +
                ", progressStatus=" + progressStatus +
                ", progressPercentage=" + progressPercentage +
                ", totalAmount=" + totalAmount +
                ", paymentStatus=" + paymentStatus +
                '}';
    }

}