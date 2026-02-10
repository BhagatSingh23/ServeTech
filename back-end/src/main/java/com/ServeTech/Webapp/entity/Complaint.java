package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.ComplaintPriority;
import com.ServeTech.Webapp.entity.enums.ComplaintStatus;
import com.ServeTech.Webapp.entity.enums.ComplaintType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// This db table will be used to track complaints filed by clients and workers
// and log data related to them to the admin
@Entity
@Table(
        name = "complaints",
        indexes = {
                @Index(name = "idx_complainant", columnList = "complainant_id"),
                @Index(name = "idx_accused", columnList = "accused_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_assignment", columnList = "assignment_id"),
                @Index(name = "idx_priority", columnList = "priority")
        }
)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique Complaint ID (format: CMP2026000001, CMP2026000002, etc.)
    @Column(nullable = false, unique = true, length = 20, name = "complaint_id")
    private String complaintId;

    // ---------- Parties Involved ----------

    // Person filing the complaint (can be worker or client)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complainant_id", nullable = false)
    private User complainant;

    // Person being complained against (can be worker or client)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accused_id", nullable = false)
    private User accused;

    // Related work assignment (optional, for work-related complaints)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private WorkAssignment assignment;

    // ---------- Complaint Details ----------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "complaint_type")
    private ComplaintType complaintType;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 3000)
    private String description;

    // Supporting evidence (URLs to uploaded images/documents)
    @Column(length = 1000, name = "evidence_urls")
    private String evidenceUrls; // Comma-separated URLs

    // Amount disputed (for payment-related complaints)
    @Column(name = "disputed_amount")
    private Double disputedAmount;

    // Date when incident occurred
    @Column(name = "incident_date")
    private LocalDateTime incidentDate;

    // ---------- Status & Priority ----------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status = ComplaintStatus.SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    // ---------- Admin Actions ----------

    // Admin assigned to handle this complaint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    @Column(length = 3000, name = "admin_notes")
    private String adminNotes;

    @Column(length = 3000, name = "resolution_details")
    private String resolutionDetails;

    @Column(length = 3000, name = "action_taken")
    private String actionTaken;

    // ---------- Response from Accused ----------

    @Column(length = 2000, name = "accused_response")
    private String accusedResponse;

    @Column(name = "accused_response_date")
    private LocalDateTime accusedResponseDate;

    // ---------- Timestamps ----------

    @Column(nullable = false, updatable = false, name = "filed_at")
    private LocalDateTime filedAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public Complaint() {
        this.status = ComplaintStatus.SUBMITTED;
        this.priority = ComplaintPriority.MEDIUM;
    }

    public Complaint(User complainant, User accused, ComplaintType complaintType,
                     String subject, String description) {
        this();
        this.complainant = complainant;
        this.accused = accused;
        this.complaintType = complaintType;
        this.subject = subject;
        this.description = description;
    }

    // ---------- Constructors ----------

    @PrePersist
    protected void onCreate() {
        filedAt = LocalDateTime.now();
        updatedAt = filedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------- Getters and Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public User getComplainant() {
        return complainant;
    }

    public void setComplainant(User complainant) {
        this.complainant = complainant;
    }

    public User getAccused() {
        return accused;
    }

    public void setAccused(User accused) {
        this.accused = accused;
    }

    public WorkAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(WorkAssignment assignment) {
        this.assignment = assignment;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceUrls() {
        return evidenceUrls;
    }

    public void setEvidenceUrls(String evidenceUrls) {
        this.evidenceUrls = evidenceUrls;
    }

    public Double getDisputedAmount() {
        return disputedAmount;
    }

    public void setDisputedAmount(Double disputedAmount) {
        this.disputedAmount = disputedAmount;
    }

    public LocalDateTime getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public User getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getAccusedResponse() {
        return accusedResponse;
    }

    public void setAccusedResponse(String accusedResponse) {
        this.accusedResponse = accusedResponse;
    }

    public LocalDateTime getAccusedResponseDate() {
        return accusedResponseDate;
    }

    public void setAccusedResponseDate(LocalDateTime accusedResponseDate) {
        this.accusedResponseDate = accusedResponseDate;
    }

    public LocalDateTime getFiledAt() {
        return filedAt;
    }

    public void setFiledAt(LocalDateTime filedAt) {
        this.filedAt = filedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    // ---------- Helper Methods ----------

    // This method should only be called by admins
    // Assign complaint to an admin for further investigation
    public void assignToAdmin(User admin) {
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can be assigned to complaints");
        }
        this.assignedAdmin = admin;
        this.status = ComplaintStatus.UNDER_REVIEW;
        this.reviewedAt = LocalDateTime.now();
    }

    // Help update the track of the application submitted
    public void startInvestigation() {
        this.status = ComplaintStatus.INVESTIGATING;
    }

    // This method should only be called by the accused
    // This will be used to add response from accused
    public void addAccusedResponse(String response) {
        this.accusedResponse = response;
        this.accusedResponseDate = LocalDateTime.now();
    }

    // This method should only be called by admins
    // This method will be used to resolve the complaint
    public void resolve(String resolutionDetails, String actionTaken) {
        this.status = ComplaintStatus.RESOLVED;
        this.resolutionDetails = resolutionDetails;
        this.actionTaken = actionTaken;
        this.resolvedAt = LocalDateTime.now();
    }

    // If rejected the complaint then call this method
    public void reject(String reason) {
        this.status = ComplaintStatus.REJECTED;
        this.resolutionDetails = reason;
        this.resolvedAt = LocalDateTime.now();
    }

    // End the complaint
    public void close() {
        this.status = ComplaintStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    // If complaint is escalated then call this method
    // This will set the priority to critical and status to escalated
    // If the seriousness of complaint exceeds our level and reaches to administration level
    public void escalate() {
        this.status = ComplaintStatus.ESCALATED;
        this.priority = ComplaintPriority.CRITICAL;
    }

    // To add evidence URLs to complaint
    public void addEvidence(String url) {
        if (this.evidenceUrls == null || this.evidenceUrls.isEmpty()) {
            this.evidenceUrls = url;
        } else {
            this.evidenceUrls += "," + url;
        }
    }

    // Transform evidence URLs to array
    public String[] getEvidenceUrlsArray() {
        if (evidenceUrls == null || evidenceUrls.isEmpty()) {
            return new String[0];
        }
        return evidenceUrls.split(",");
    }

    // If complaint is payment-related then return true
    public boolean isPaymentRelated() {
        return complaintType == ComplaintType.UNFAIR_PAYMENT ||
                complaintType == ComplaintType.DELAYED_PAYMENT ||
                complaintType == ComplaintType.INCOMPLETE_PAYMENT;
    }

    // If complaint is open then return true
    public boolean isOpen() {
        return status == ComplaintStatus.SUBMITTED ||
                status == ComplaintStatus.UNDER_REVIEW ||
                status == ComplaintStatus.INVESTIGATING;
    }

    // Used to calculate the number of days since complaint was filed
    public long getDaysSinceFiled() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                filedAt.toLocalDate(),
                LocalDateTime.now().toLocalDate()
        );
    }


    // Custom toString method
    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", complaintId='" + complaintId + '\'' +
                ", complaintType=" + complaintType +
                ", status=" + status +
                ", priority=" + priority +
                ", filedAt=" + filedAt +
                '}';
    }
}
