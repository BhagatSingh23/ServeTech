package com.ServeTech.Webapp.entity.enums;

// This defines the status of a work request
public enum WorkRequestStatus {
    DRAFT,          // Saved but not published
    OPEN,           // Just posted, accepting applications
    IN_PROGRESS,    // Worker assigned and working
    COMPLETED,      // Work finished
    CANCELLED,      // Cancelled by client
    CLOSED          // Archived
}