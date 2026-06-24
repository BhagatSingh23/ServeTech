package com.ServeTech.Webapp.dto.response;

import java.util.List;

public class ClientDashboardResponse {

    private DashboardStats stats;
    private List<WorkRequestResponse> activeJobs;
    private List<RecentWorkerDTO> recentWorkers;

    public ClientDashboardResponse() {
    }

    // Getters and Setters
    public DashboardStats getStats() { return stats; }
    public void setStats(DashboardStats stats) { this.stats = stats; }
    public List<WorkRequestResponse> getActiveJobs() { return activeJobs; }
    public void setActiveJobs(List<WorkRequestResponse> activeJobs) { this.activeJobs = activeJobs; }
    public List<RecentWorkerDTO> getRecentWorkers() { return recentWorkers; }
    public void setRecentWorkers(List<RecentWorkerDTO> recentWorkers) { this.recentWorkers = recentWorkers; }

    // Inner class for dashboard statistics
    public static class DashboardStats {
        private long projectsPosted;
        private long activeProjects;
        private long workersHired;
        private Double totalSpent;

        public DashboardStats() {
        }

        public long getProjectsPosted() { return projectsPosted; }
        public void setProjectsPosted(long projectsPosted) { this.projectsPosted = projectsPosted; }
        public long getActiveProjects() { return activeProjects; }
        public void setActiveProjects(long activeProjects) { this.activeProjects = activeProjects; }
        public long getWorkersHired() { return workersHired; }
        public void setWorkersHired(long workersHired) { this.workersHired = workersHired; }
        public Double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
    }

    // Inner class for recent worker info
    public static class RecentWorkerDTO {
        private String workerName;
        private String assignmentId;
        private String status;
        private String paymentStatus;
        private Double agreedWage;
        private Double totalPaid;
        private Double pendingAmount;
        private String jobTitle;

        public RecentWorkerDTO() {
        }

        public String getWorkerName() { return workerName; }
        public void setWorkerName(String workerName) { this.workerName = workerName; }
        public String getAssignmentId() { return assignmentId; }
        public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public Double getAgreedWage() { return agreedWage; }
        public void setAgreedWage(Double agreedWage) { this.agreedWage = agreedWage; }
        public Double getTotalPaid() { return totalPaid; }
        public void setTotalPaid(Double totalPaid) { this.totalPaid = totalPaid; }
        public Double getPendingAmount() { return pendingAmount; }
        public void setPendingAmount(Double pendingAmount) { this.pendingAmount = pendingAmount; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    }
}
