package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// This db table stores client-specific information
// This will help fetch client-specific information for the admin dashboard
@Entity
@Table(name = "client_profiles")
public class ClientProfile {

    @Id
    private Long id; // Same as User ID

    // One-to-One Relationship mapping with User
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // ---------- Business Information ----------
    @Column(length = 200, name = "company_name")
    private String companyName;


    // ---------- Statistics (for Dashboard) ----------
    @Column(name = "total_work_requests_posted", nullable = false)
    private Integer totalWorkRequestsPosted = 0;

    @Column(name = "total_active_requests", nullable = false)
    private Integer totalActiveRequests = 0;

    @Column(name = "total_completed_requests", nullable = false)
    private Integer totalCompletedRequests = 0;

    @Column(name = "total_workers_hired", nullable = false)
    private Integer totalWorkersHired = 0;

    // Average rating from workers (1.0 to 5.0)
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_ratings", nullable = false)
    private Integer totalRatings = 0;

    // Total money spent on hiring workers
    @Column(name = "total_amount_spent")
    private Double totalAmountSpent = 0.0;

    // ---------- Verification ----------
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    // ---------- Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ClientProfile() {
        this.totalWorkRequestsPosted = 0;
        this.totalActiveRequests = 0;
        this.totalCompletedRequests = 0;
        this.totalWorkersHired = 0;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.totalAmountSpent = 0.0;
        this.isVerified = false;
    }

    public ClientProfile(User user) {
        this();
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getTotalWorkRequestsPosted() {
        return totalWorkRequestsPosted;
    }

    public void setTotalWorkRequestsPosted(Integer totalWorkRequestsPosted) {
        this.totalWorkRequestsPosted = totalWorkRequestsPosted;
    }

    public Integer getTotalActiveRequests() {
        return totalActiveRequests;
    }

    public void setTotalActiveRequests(Integer totalActiveRequests) {
        this.totalActiveRequests = totalActiveRequests;
    }

    public Integer getTotalCompletedRequests() {
        return totalCompletedRequests;
    }

    public void setTotalCompletedRequests(Integer totalCompletedRequests) {
        this.totalCompletedRequests = totalCompletedRequests;
    }

    public Integer getTotalWorkersHired() {
        return totalWorkersHired;
    }

    public void setTotalWorkersHired(Integer totalWorkersHired) {
        this.totalWorkersHired = totalWorkersHired;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Double getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(Double totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
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

    // Helper Methods

    // Method to update client's ratings when new rating is posted
    public void updateRating(Double newRating) {
        if (newRating == null || newRating < 1.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }

        double totalRatingSum = (averageRating * totalRatings) + newRating;
        totalRatings++;
        averageRating = totalRatingSum / totalRatings;
    }

    // Increment work requests counter
    public void incrementWorkRequestsPosted() {
        totalWorkRequestsPosted++;
        totalActiveRequests++;
    }

    // Increment of total completed requests
    public void incrementCompletedRequests() {
        totalCompletedRequests++;
        if (totalActiveRequests > 0) {
            totalActiveRequests--;
        }
    }

    // Track of workers hired till date
    public void incrementWorkersHired() {
        totalWorkersHired++;
    }

    // Total amount spent on hiring workers
    public void addToAmountSpent(Double amount) {
        if (amount != null && amount > 0) {
            this.totalAmountSpent += amount;
        }
    }

    @Override
    public String toString() {
        return "ClientProfile{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", totalWorkRequestsPosted=" + totalWorkRequestsPosted +
                ", totalWorkersHired=" + totalWorkersHired +
                ", averageRating=" + averageRating +
                ", isVerified=" + isVerified +
                '}';
    }
}
