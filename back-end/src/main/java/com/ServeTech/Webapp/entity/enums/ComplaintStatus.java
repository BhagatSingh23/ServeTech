package com.ServeTech.Webapp.entity.enums;

// This will be used to track the status of a complaint
// Also used to show the status of a complaint to the user
public enum ComplaintStatus {
    SUBMITTED,      // Just filed by user
    UNDER_REVIEW,   // Admin reviewing the complaint
    INVESTIGATING,  // Investigation in progress
    RESOLVED,       // Complaint resolved
    REJECTED,       // Complaint rejected
    CLOSED,         // Complaint closed
    ESCALATED       // Escalated to higher authority
}