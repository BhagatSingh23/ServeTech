package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.*;

public class SubmitRatingRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1", message = "Rating must be at least 1")
    @DecimalMax(value = "5", message = "Rating must be at most 5")
    private Double rating;

    @Size(max = 1000, message = "Review must not exceed 1000 characters")
    private String review;

    @DecimalMin(value = "1", message = "Skill rating must be at least 1")
    @DecimalMax(value = "5", message = "Skill rating must be at most 5")
    private Double skillRating;

    @DecimalMin(value = "1", message = "Professionalism rating must be at least 1")
    @DecimalMax(value = "5", message = "Professionalism rating must be at most 5")
    private Double professionalismRating;

    @DecimalMin(value = "1", message = "Punctuality rating must be at least 1")
    @DecimalMax(value = "5", message = "Punctuality rating must be at most 5")
    private Double punctualityRating;

    public SubmitRatingRequest() {
    }

    // Getters and Setters
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
}
