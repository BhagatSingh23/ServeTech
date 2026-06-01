package com.ServeTech.Webapp.dto.response;

import java.util.List;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalWorkers;
    private long totalClients;
    private long totalAssignments;
    private long totalWorkRequests;
    private long openWorkRequests;
    private long pendingVerifications;
    private long pendingComplaints;
    private Double totalRevenue;
    private List<WorkerVerificationDTO> pendingVerificationsList;

    public AdminDashboardResponse() {
    }

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalWorkers() { return totalWorkers; }
    public void setTotalWorkers(long totalWorkers) { this.totalWorkers = totalWorkers; }
    public long getTotalClients() { return totalClients; }
    public void setTotalClients(long totalClients) { this.totalClients = totalClients; }
    public long getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(long totalAssignments) { this.totalAssignments = totalAssignments; }
    public long getTotalWorkRequests() { return totalWorkRequests; }
    public void setTotalWorkRequests(long totalWorkRequests) { this.totalWorkRequests = totalWorkRequests; }
    public long getOpenWorkRequests() { return openWorkRequests; }
    public void setOpenWorkRequests(long openWorkRequests) { this.openWorkRequests = openWorkRequests; }
    public long getPendingVerifications() { return pendingVerifications; }
    public void setPendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; }
    public long getPendingComplaints() { return pendingComplaints; }
    public void setPendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public List<WorkerVerificationDTO> getPendingVerificationsList() { return pendingVerificationsList; }
    public void setPendingVerificationsList(List<WorkerVerificationDTO> pendingVerificationsList) { this.pendingVerificationsList = pendingVerificationsList; }

    // Inner class for pending worker verifications
    public static class WorkerVerificationDTO {
        private Long userId;
        private String name;
        private String phoneNumber;
        private String pincode;
        private List<String> skills;

        public WorkerVerificationDTO() {
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
    }
}
