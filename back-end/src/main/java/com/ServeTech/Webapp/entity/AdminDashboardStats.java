package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// This is the entity class for the admin dashboard statistics
// To display the data for the admin's dashboard will be fetched from this table
@Entity
@Table(name = "admin_dashboard_stats")
public class AdminDashboardStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "stats_date")
    private LocalDate statsDate;

    // -- User Statistics (User of the webapp Servetech) --
    @Column(name = "total_users", nullable = false)
    private Integer totalUsers = 0;

    @Column(name = "total_workers", nullable = false)
    private Integer totalWorkers = 0;

    @Column(name = "total_clients", nullable = false)
    private Integer totalClients = 0;

    @Column(name = "active_users", nullable = false)
    private Integer activeUsers = 0;

    @Column(name = "new_registrations_today", nullable = false)
    private Integer newRegistrationsToday = 0;

    // --- Work Statistics (Business on Servetech) ---
    @Column(name = "total_work_requests", nullable = false)
    private Integer totalWorkRequests = 0;

    @Column(name = "active_work_requests", nullable = false)
    private Integer activeWorkRequests = 0;

    @Column(name = "completed_work_requests", nullable = false)
    private Integer completedWorkRequests = 0;

    @Column(name = "total_assignments", nullable = false)
    private Integer totalAssignments = 0;

    @Column(name = "active_assignments", nullable = false)
    private Integer activeAssignments = 0;

    // -- Financial Statistics (Data regarding payments) --
    @Column(name = "total_transactions", nullable = false)
    private Integer totalTransactions = 0;

    @Column(name = "total_revenue")
    private Double totalRevenue = 0.0;

    @Column(name = "pending_payments")
    private Double pendingPayments = 0.0;

    // ---------- Skills Statistics ----------
    @Column(name = "most_demanded_skill")
    private String mostDemandedSkill;

    @Column(name = "least_demanded_skill")
    private String leastDemandedSkill;


    // Constructors
    public AdminDashboardStats() {
        this.statsDate = LocalDate.now();
    }

    public AdminDashboardStats(LocalDate statsDate) {
        this.statsDate = statsDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(LocalDate statsDate) {
        this.statsDate = statsDate;
    }

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getTotalWorkers() {
        return totalWorkers;
    }

    public void setTotalWorkers(Integer totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public Integer getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(Integer totalClients) {
        this.totalClients = totalClients;
    }

    public Integer getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Integer activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Integer getNewRegistrationsToday() {
        return newRegistrationsToday;
    }

    public void setNewRegistrationsToday(Integer newRegistrationsToday) {
        this.newRegistrationsToday = newRegistrationsToday;
    }

    public Integer getTotalWorkRequests() {
        return totalWorkRequests;
    }

    public void setTotalWorkRequests(Integer totalWorkRequests) {
        this.totalWorkRequests = totalWorkRequests;
    }

    public Integer getActiveWorkRequests() {
        return activeWorkRequests;
    }

    public void setActiveWorkRequests(Integer activeWorkRequests) {
        this.activeWorkRequests = activeWorkRequests;
    }

    public Integer getCompletedWorkRequests() {
        return completedWorkRequests;
    }

    public void setCompletedWorkRequests(Integer completedWorkRequests) {
        this.completedWorkRequests = completedWorkRequests;
    }

    public Integer getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(Integer totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public Integer getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(Integer activeAssignments) {
        this.activeAssignments = activeAssignments;
    }

    public Integer getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(Double pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public String getMostDemandedSkill() {
        return mostDemandedSkill;
    }

    public void setMostDemandedSkill(String mostDemandedSkill) {
        this.mostDemandedSkill = mostDemandedSkill;
    }

    public String getLeastDemandedSkill() {
        return leastDemandedSkill;
    }

    public void setLeastDemandedSkill(String leastDemandedSkill) {
        this.leastDemandedSkill = leastDemandedSkill;
    }

    // toString() Method can be customized as per the requirement
    @Override
    public String toString() {
        return "AdminDashboardStats{" +
                "id=" + id +
                ", statsDate=" + statsDate +
                ", totalUsers=" + totalUsers +
                ", totalWorkers=" + totalWorkers +
                ", totalClients=" + totalClients +
                ", totalWorkRequests=" + totalWorkRequests +
                '}';
    }
}