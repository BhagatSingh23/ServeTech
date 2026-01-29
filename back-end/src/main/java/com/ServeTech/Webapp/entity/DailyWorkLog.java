package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// This db table stores daily work logs for each worker
@Entity
@Table(
        name = "daily_work_logs",
        indexes = {
                @Index(name = "idx_assignment", columnList = "assignment_id"),
                @Index(name = "idx_date", columnList = "work_date")
        }
)
public class DailyWorkLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One Relationship mapping with WorkAssignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private WorkAssignment assignment;

    @Column(nullable = false, name = "work_date")
    private LocalDate workDate;

    // ---------- Attendance ----------
    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "total_hours_worked")
    private Double totalHoursWorked;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = false;

    // ---------- Work Details ----------
    @Column(length = 1000, name = "work_description")
    private String workDescription;

    @Column(length = 500, name = "materials_used")
    private String materialsUsed;

    @Column(name = "progress_today")
    private Integer progressToday = 0;

    // ---------- Images ----------
    @Column(length = 500, name = "work_image_url")
    private String workImageUrl;

    // ---------- Payment ----------
    @Column(name = "wage_for_day")
    private Double wageForDay;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    // ---------- Verification ----------
    @Column(name = "client_verified", nullable = false)
    private Boolean clientVerified = false;

    @Column(name = "client_notes", length = 500)
    private String clientNotes;

    // ---------- Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public DailyWorkLog() {
        this.isPresent = false;
        this.isPaid = false;
        this.clientVerified = false;
        this.progressToday = 0;
    }

    public DailyWorkLog(WorkAssignment assignment, LocalDate workDate) {
        this();
        this.assignment = assignment;
        this.workDate = workDate;
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

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(Double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public String getMaterialsUsed() {
        return materialsUsed;
    }

    public void setMaterialsUsed(String materialsUsed) {
        this.materialsUsed = materialsUsed;
    }

    public Integer getProgressToday() {
        return progressToday;
    }

    public void setProgressToday(Integer progressToday) {
        this.progressToday = progressToday;
    }

    public String getWorkImageUrl() {
        return workImageUrl;
    }

    public void setWorkImageUrl(String workImageUrl) {
        this.workImageUrl = workImageUrl;
    }

    public Double getWageForDay() {
        return wageForDay;
    }

    public void setWageForDay(Double wageForDay) {
        this.wageForDay = wageForDay;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Boolean getClientVerified() {
        return clientVerified;
    }

    public void setClientVerified(Boolean clientVerified) {
        this.clientVerified = clientVerified;
    }

    public String getClientNotes() {
        return clientNotes;
    }

    public void setClientNotes(String clientNotes) {
        this.clientNotes = clientNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper Methods

    // Marking worker as present
    public void markAttendance(LocalTime checkIn) {
        this.isPresent = true;
        this.checkInTime = checkIn;
    }

    // This method calculates the total hours worked by the worker
    public void checkOut(LocalTime checkOut) {
        this.checkOutTime = checkOut;
        if (this.checkInTime != null) {
            long minutes = java.time.Duration.between(checkInTime, checkOut).toMinutes();
            this.totalHoursWorked = minutes / 60.0;
        }
    }

    // This method marks the work as paid
    public void verifyByClient(Boolean verified, String notes) {
        this.clientVerified = verified;
        this.clientNotes = notes;
    }

    @Override
    public String toString() {
        return "DailyWorkLog{" +
                "id=" + id +
                ", workDate=" + workDate +
                ", isPresent=" + isPresent +
                ", totalHoursWorked=" + totalHoursWorked +
                ", isPaid=" + isPaid +
                '}';
    }
}
