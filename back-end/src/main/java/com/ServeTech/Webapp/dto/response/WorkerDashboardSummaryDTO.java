package com.ServeTech.Webapp.dto.response;

import java.util.List;

// DTO for Worker Dashboard Summary - represents a summary of the worker's dashboard
public class WorkerDashboardSummaryDTO {

    // Summary Statistics
    private Double totalEarnings;
    private Double pendingPayments;
    private Integer totalJobsCompleted;
    private Integer totalJobsInProgress;
    private Double averageRating;
    private Integer totalRatings;

    // Current Status
    private Boolean availableForWork;
    private Integer activeAssignments;

    // Bookings List
    private List<WorkerDashboardDTO> previousBookings;
    private List<WorkerDashboardDTO> currentBookings;
    private List<WorkerDashboardDTO> upcomingBookings;

    // Constructors
    public WorkerDashboardSummaryDTO() {
    }

    // Getters and Setters
    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(Double pendingPayments) {
        this.pendingPayments = pendingPayments;
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

    public Boolean getAvailableForWork() {
        return availableForWork;
    }

    public void setAvailableForWork(Boolean availableForWork) {
        this.availableForWork = availableForWork;
    }

    public Integer getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(Integer activeAssignments) {
        this.activeAssignments = activeAssignments;
    }

    public List<WorkerDashboardDTO> getPreviousBookings() {
        return previousBookings;
    }

    public void setPreviousBookings(List<WorkerDashboardDTO> previousBookings) {
        this.previousBookings = previousBookings;
    }

    public List<WorkerDashboardDTO> getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(List<WorkerDashboardDTO> currentBookings) {
        this.currentBookings = currentBookings;
    }

    public List<WorkerDashboardDTO> getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(List<WorkerDashboardDTO> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }
}
