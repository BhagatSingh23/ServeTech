package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Rating;

import java.time.LocalDateTime;

public class RatingResponse {

    private Long id;
    private String raterName;
    private Long raterId;
    private String rateeName;
    private Long rateeId;
    private Long assignmentId;
    private Double rating;
    private String review;
    private Double skillRating;
    private Double professionalismRating;
    private Double punctualityRating;
    private LocalDateTime createdAt;

    public RatingResponse() {
    }

    public static RatingResponse fromEntity(Rating r) {
        RatingResponse response = new RatingResponse();
        response.setId(r.getId());
        response.setRaterName(r.getRater().getFullName());
        response.setRaterId(r.getRater().getId());
        response.setRateeName(r.getRatee().getFullName());
        response.setRateeId(r.getRatee().getId());
        response.setAssignmentId(r.getAssignment().getId());
        response.setRating(r.getRating());
        response.setReview(r.getReview());
        response.setSkillRating(r.getSkillRating());
        response.setProfessionalismRating(r.getProfessionalismRating());
        response.setPunctualityRating(r.getPunctualityRating());
        response.setCreatedAt(r.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRaterName() { return raterName; }
    public void setRaterName(String raterName) { this.raterName = raterName; }
    public Long getRaterId() { return raterId; }
    public void setRaterId(Long raterId) { this.raterId = raterId; }
    public String getRateeName() { return rateeName; }
    public void setRateeName(String rateeName) { this.rateeName = rateeName; }
    public Long getRateeId() { return rateeId; }
    public void setRateeId(Long rateeId) { this.rateeId = rateeId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public Double getSkillRating() { return skillRating; }
    public void setSkillRating(Double skillRating) { this.skillRating = skillRating; }
    public Double getProfessionalismRating() { return professionalismRating; }
    public void setProfessionalismRating(Double professionalismRating) { this.professionalismRating = professionalismRating; }
    public Double getPunctualityRating() { return punctualityRating; }
    public void setPunctualityRating(Double punctualityRating) { this.punctualityRating = punctualityRating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
