package com.ServeTech.Webapp.entity.enums;

// This enum represents the different types of complaints that can be filed
// New types can be added as needed
public enum ComplaintType {
    UNFAIR_PAYMENT,          // Payment disputes
    DELAYED_PAYMENT,         // Payment not received on time
    INCOMPLETE_PAYMENT,      // Partial payment issues
    POOR_WORK_QUALITY,       // Quality of work not satisfactory
    UNPROFESSIONAL_BEHAVIOR, // Behavioral issues
    SAFETY_VIOLATION,        // Safety concerns at work site
    CONTRACT_BREACH,         // Terms not followed
    HARASSMENT,              // Any form of harassment
    DISCRIMINATION,          // Discrimination based on any grounds
    WORK_ENVIRONMENT,        // Poor working conditions
    COMMUNICATION_ISSUES,    // Lack of proper communication
    OTHER                    // Other issues
}