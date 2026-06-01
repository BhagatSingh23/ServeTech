package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

public class UpdateWorkerProfileRequest {

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private List<Long> skillIds;

    @DecimalMin(value = "0", message = "Daily wage must be a positive value")
    private Double dailyWage;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
    private String pincode;

    private Integer experienceYears;

    public UpdateWorkerProfileRequest() {
    }

    // Getters and Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(List<Long> skillIds) { this.skillIds = skillIds; }
    public Double getDailyWage() { return dailyWage; }
    public void setDailyWage(Double dailyWage) { this.dailyWage = dailyWage; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
}
