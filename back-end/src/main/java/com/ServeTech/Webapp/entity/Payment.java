package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// This db table stores payments made by clients to workers
// This will help fetch payments for the admin dashboard
// This will help to fetch payments for the worker's profile in real-time'
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_assignment", columnList = "assignment_id"),
                @Index(name = "idx_status", columnList = "payment_status")
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique Transaction ID (format: TXN2026000001)
    @Column(nullable = false, unique = true, length = 50, name = "transaction_id")
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private WorkAssignment assignment;

    // Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    // Worker
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // CASH, UPI, BANK_TRANSFER
    @Column(length = 50, name = "payment_method")
    private String paymentMethod;

    // UPI transaction ID, etc.
    @Column(length = 100, name = "payment_reference")
    private String paymentReference;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Constructors
    public Payment() {
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Payment(WorkAssignment assignment, User payer, User payee, Double amount) {
        this();
        this.assignment = assignment;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public WorkAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(WorkAssignment assignment) {
        this.assignment = assignment;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    // Helper Methods

    // Mark payment as paid
    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    // Mark payment as failed
    public void markAsFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }


    // Custom toString method
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}