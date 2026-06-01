package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.*;

public class RecordPaymentRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1", message = "Amount must be at least 1")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentReference;

    private String paymentNotes;

    public RecordPaymentRequest() {
    }

    // Getters and Setters
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public String getPaymentNotes() { return paymentNotes; }
    public void setPaymentNotes(String paymentNotes) { this.paymentNotes = paymentNotes; }
}
