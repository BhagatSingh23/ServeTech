package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkerProfile;

import java.util.List;
import java.util.stream.Collectors;

public class WorkerProfileResponse {

    private Long userId;
    private String uniqueUserId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String pincode;
    private String block;
    private String district;
    private String state;
    private String bio;
    private Integer experienceYears;
    private Double dailyWage;
    private Boolean availableForWork;
    private Boolean isVerified;
    private List<String> skills;
    private Integer totalJobsCompleted;
    private Integer totalJobsInProgress;
    private Double totalEarnings;
    private Double averageRating;
    private Integer totalRatings;
    private Double successRate;

    public WorkerProfileResponse() {
    }

    public static WorkerProfileResponse fromEntity(WorkerProfile profile) {
        WorkerProfileResponse response = new WorkerProfileResponse();
        User user = profile.getUser();

        response.setUserId(user.getId());
        response.setUniqueUserId(user.getUniqueUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setPincode(user.getPincode());
        response.setBlock(user.getBlock());
        response.setDistrict(user.getDistrict());
        response.setState(user.getState());
        response.setBio(profile.getBio());
        response.setExperienceYears(profile.getExperienceYears());
        response.setDailyWage(profile.getDailyWage());
        response.setAvailableForWork(profile.getAvailableForWork());
        response.setIsVerified(profile.getIsVerified());
        response.setTotalJobsCompleted(profile.getTotalJobsCompleted());
        response.setTotalJobsInProgress(profile.getTotalJobsInProgress());
        response.setTotalEarnings(profile.getTotalEarnings());
        response.setAverageRating(profile.getAverageRating());
        response.setTotalRatings(profile.getTotalRatings());
        response.setSuccessRate(profile.getSuccessRate());

        if (profile.getSkills() != null) {
            response.setSkills(profile.getSkills().stream()
                    .map(skill -> skill.getName().name())
                    .collect(Collectors.toList()));
        }

        return response;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUniqueUserId() { return uniqueUserId; }
    public void setUniqueUserId(String uniqueUserId) { this.uniqueUserId = uniqueUserId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public Double getDailyWage() { return dailyWage; }
    public void setDailyWage(Double dailyWage) { this.dailyWage = dailyWage; }
    public Boolean getAvailableForWork() { return availableForWork; }
    public void setAvailableForWork(Boolean availableForWork) { this.availableForWork = availableForWork; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Integer getTotalJobsCompleted() { return totalJobsCompleted; }
    public void setTotalJobsCompleted(Integer totalJobsCompleted) { this.totalJobsCompleted = totalJobsCompleted; }
    public Integer getTotalJobsInProgress() { return totalJobsInProgress; }
    public void setTotalJobsInProgress(Integer totalJobsInProgress) { this.totalJobsInProgress = totalJobsInProgress; }
    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
}
