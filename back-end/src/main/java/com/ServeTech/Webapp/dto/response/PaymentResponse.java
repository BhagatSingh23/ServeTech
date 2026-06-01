package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Payment;
import com.ServeTech.Webapp.entity.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String transactionId;
    private Long assignmentId;
    private String payerName;
    private String payeeName;
    private Double amount;
    private String paymentMethod;
    private String paymentReference;
    private String paymentNotes;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public static PaymentResponse fromEntity(Payment p) {
        PaymentResponse response = new PaymentResponse();
        response.setId(p.getId());
        response.setTransactionId(p.getTransactionId());
        response.setAssignmentId(p.getAssignment().getId());
        response.setPayerName(p.getPayer().getFullName());
        response.setPayeeName(p.getPayee().getFullName());
        response.setAmount(p.getAmount());
        response.setPaymentMethod(p.getPaymentMethod());
        response.setPaymentReference(p.getPaymentReference());
        response.setPaymentNotes(p.getNotes());
        response.setPaymentStatus(p.getPaymentStatus());
        response.setPaidAt(p.getPaidAt());
        response.setCreatedAt(p.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
