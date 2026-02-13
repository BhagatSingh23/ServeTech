package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO for Worker Dashboard - represents a single assignment
public class WorkerDashboardDTO {

    // Assignment Information
    private Long assignmentId;
    private String assignmentNumber; // WA2026000001

    // Employer (Client) Information
    private String employerName;
    private String employerPhone;
    private String employerLocation;

    // Work Details
    private String role; // Primary skill name
    private String workTitle;
    private String workDescription;

    // Dates
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private Integer daysWorked;

    // Location
    private String workLocation; // City or full address
    private String pincode;

    // Payment
    private Double agreedWagePerDay;
    private Double totalAmount;
    private Double amountPaid;
    private Double amountPending;
    private PaymentStatus paymentStatus;

    // Progress
    private WorkProgressStatus progressStatus;
    private Integer progressPercentage;

    // Rating & Review
    private Double clientRating;
    private String clientReview;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Constructors
    public WorkerDashboardDTO() {
    }

    // Getters and Setters
    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentNumber() {
        return assignmentNumber;
    }

    public void setAssignmentNumber(String assignmentNumber) {
        this.assignmentNumber = assignmentNumber;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerPhone() {
        return employerPhone;
    }

    public void setEmployerPhone(String employerPhone) {
        this.employerPhone = employerPhone;
    }

    public String getEmployerLocation() {
        return employerLocation;
    }

    public void setEmployerLocation(String employerLocation) {
        this.employerLocation = employerLocation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Integer getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(Integer daysWorked) {
        this.daysWorked = daysWorked;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Double getAgreedWagePerDay() {
        return agreedWagePerDay;
    }

    public void setAgreedWagePerDay(Double agreedWagePerDay) {
        this.agreedWagePerDay = agreedWagePerDay;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountPending() {
        return amountPending;
    }

    public void setAmountPending(Double amountPending) {
        this.amountPending = amountPending;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public WorkProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(WorkProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Double getClientRating() {
        return clientRating;
    }

    public void setClientRating(Double clientRating) {
        this.clientRating = clientRating;
    }

    public String getClientReview() {
        return clientReview;
    }

    public void setClientReview(String clientReview) {
        this.clientReview = clientReview;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
