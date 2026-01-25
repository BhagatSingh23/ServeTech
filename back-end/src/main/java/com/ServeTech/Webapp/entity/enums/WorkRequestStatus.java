package com.ServeTech.Webapp.entity.enums;

// This is defines the schedule of the worker
public enum WorkRequestStatus {
    OPEN,           // Just posted
    IN_PROGRESS,    // Worker assigned and working
    COMPLETED,      // Work finished
    CANCELLED,      // Cancelled by client
    CLOSED          // Archived
}