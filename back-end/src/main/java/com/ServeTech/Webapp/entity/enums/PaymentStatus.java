package com.ServeTech.Webapp.entity.enums;

// Payment status to be updated in the payment table
public enum PaymentStatus {
    PENDING,
    PAID,
    PARTIALLY_PAID,     // If the amount paid is less than the total amount
    FAILED,
    REFUNDED
}