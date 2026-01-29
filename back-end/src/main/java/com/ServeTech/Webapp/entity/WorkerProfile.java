package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// This db table stores worker-specific information
// This will help fetch worker-specific information for the admin dashboard
// This will help to fetch worker-specific information for the worker's profile in real-time'
@Entity
@Table(name = "worker_profiles")
public class WorkerProfile {

    @Id
    private Long id; // Same as User ID

    // One worker profile belongs to one user
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // ---------- Skills (Many-to-Many) ----------
    // Worker can have multiple skills
    // Creates join table: worker_skills
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "worker_skills",
            joinColumns = @JoinColumn(name = "worker_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    // ---------- Professional Information ----------
    @Column(length = 1000)
    private String bio;

    @Column(name = "experience_years")
    private Integer experienceYears;

    // Expected daily wage in rupees
    @Column(name = "daily_wage")
    private Double dailyWage;

    @Column(name = "available_for_work", nullable = false)
    private Boolean availableForWork = true;

    // ---------- Statistics (for Dashboard) ----------
    @Column(name = "total_jobs_completed", nullable = false)
    private Integer totalJobsCompleted = 0;

    @Column(name = "total_jobs_in_progress", nullable = false)
    private Integer totalJobsInProgress = 0;

    // Average rating from clients (1.0 to 5.0)
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_ratings", nullable = false)
    private Integer totalRatings = 0;

    // Total money earned
    @Column(name = "total_earnings")
    private Double totalEarnings = 0.0;

    // Percentage of successfully completed jobs
    @Column(name = "success_rate")
    private Double successRate = 0.0;

    // ---------- Verification ----------
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    // URL to uploaded Aadhar/ID proof
    @Column(name = "verification_document_url", length = 500)
    private String verificationDocumentUrl;

    // ---------- Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

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
    public WorkerProfile() {
        this.skills = new HashSet<>();
        this.totalJobsCompleted = 0;
        this.totalJobsInProgress = 0;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.totalEarnings = 0.0;
        this.successRate = 0.0;
        this.availableForWork = true;
        this.isVerified = false;
    }

    public WorkerProfile(User user) {
        this();
        this.user = user;
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

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Double getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(Double dailyWage) {
        this.dailyWage = dailyWage;
    }

    public Boolean getAvailableForWork() {
        return availableForWork;
    }

    public void setAvailableForWork(Boolean availableForWork) {
        this.availableForWork = availableForWork;
    }

    public Integer getTotalJobsCompleted() {
        return totalJobsCompleted;
    }

    public void setTotalJobsCompleted(Integer totalJobsCompleted) {
        this.totalJobsCompleted = totalJobsCompleted;
    }

    public Integer getTotalJobsInProgress() {
        return totalJobsInProgress;
    }

    public void setTotalJobsInProgress(Integer totalJobsInProgress) {
        this.totalJobsInProgress = totalJobsInProgress;
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

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getVerificationDocumentUrl() {
        return verificationDocumentUrl;
    }

    public void setVerificationDocumentUrl(String verificationDocumentUrl) {
        this.verificationDocumentUrl = verificationDocumentUrl;
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

   // Add a skill to worker's skillset
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    // Remove a skill from worker's skillset
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    // Update worker's rating
    public void updateRating(Double newRating) {
        if (newRating == null || newRating < 1.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }

        // Calculate new average: (old_avg * old_count + new_rating) / (old_count + 1)
        double totalRatingSum = (averageRating * totalRatings) + newRating;
        totalRatings++;
        averageRating = totalRatingSum / totalRatings;
    }

    // Completed job assignment up-to-date counters
    public void incrementCompletedJobs() {
        totalJobsCompleted++;
        updateSuccessRate();
    }

    // Increment in-progress jobs counter
    public void incrementInProgressJobs() {
        totalJobsInProgress++;
    }

    // Decrement in-progress jobs counter
    public void decrementInProgressJobs() {
        if (totalJobsInProgress > 0) {
            totalJobsInProgress--;
        }
    }

    // Add earnings to worker's total earnings
    public void addEarnings(Double amount) {
        if (amount != null && amount > 0) {
            this.totalEarnings += amount;
        }
    }

    // Success rate calculation
    private void updateSuccessRate() {
        int totalJobs = totalJobsCompleted + totalJobsInProgress;
        if (totalJobs > 0) {
            successRate = (totalJobsCompleted * 100.0) / totalJobs;
        }
    }

    // Custom toString method
    @Override
    public String toString() {
        return "WorkerProfile{" +
                "id=" + id +
                ", experienceYears=" + experienceYears +
                ", dailyWage=" + dailyWage +
                ", totalJobsCompleted=" + totalJobsCompleted +
                ", averageRating=" + averageRating +
                ", isVerified=" + isVerified +
                '}';
    }
}
