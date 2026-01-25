package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Seperate rating table for workers and clients
@Entity
@Table(
        name = "ratings",
        indexes = {
                @Index(name = "idx_rater", columnList = "rater_id"),
                @Index(name = "idx_ratee", columnList = "ratee_id")
        }
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private WorkAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater; // Person giving rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ratee_id", nullable = false)
    private User ratee; // Person being rated

    @Column(nullable = false)
    private Double rating; // 1.0 to 5.0

    @Column(length = 1000)
    private String review;

    // Specific rating categories (for workers)
    @Column(name = "skill_rating")
    private Double skillRating;

    @Column(name = "professionalism_rating")
    private Double professionalismRating;

    @Column(name = "punctuality_rating")
    private Double punctualityRating;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Rating() {
    }

    public Rating(WorkAssignment assignment, User rater, User ratee, Double rating) {
        this.assignment = assignment;
        this.rater = rater;
        this.ratee = ratee;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(WorkAssignment assignment) {
        this.assignment = assignment;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }

    public User getRatee() {
        return ratee;
    }

    public void setRatee(User ratee) {
        this.ratee = ratee;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Double getSkillRating() {
        return skillRating;
    }

    public void setSkillRating(Double skillRating) {
        this.skillRating = skillRating;
    }

    public Double getProfessionalismRating() {
        return professionalismRating;
    }

    public void setProfessionalismRating(Double professionalismRating) {
        this.professionalismRating = professionalismRating;
    }

    public Double getPunctualityRating() {
        return punctualityRating;
    }

    public void setPunctualityRating(Double punctualityRating) {
        this.punctualityRating = punctualityRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                '}';
    }
}