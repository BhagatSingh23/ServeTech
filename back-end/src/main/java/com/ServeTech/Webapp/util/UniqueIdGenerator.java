package com.ServeTech.Webapp.util;

import org.springframework.stereotype.Component;
import java.time.Year;
import java.util.UUID;

// This class is used to generate unique IDs for users, work requests, work assignments and transactions
@Component
public class UniqueIdGenerator {

    // UUID-based generation to prevent duplicate IDs after application restart
    private String generateId(String prefix) {
        int currentYear = Year.now().getValue();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        return prefix + currentYear + uuid;
    }

    public String generateUniqueUserId() {
        return generateId("");
    }

    // Work request ID format: WR + YEAR + 8-char UUID
    public String generateWorkRequestId() {
        return generateId("WR");
    }

    // Generate work assignment ID
    public String generateWorkAssignmentId() {
        return generateId("WA");
    }

    // Transaction ID format: TXN + YEAR + 8-char UUID
    public String generateTransactionId() {
        return generateId("TXN");
    }

    // Complaint ID format: CMP + YEAR + 8-char UUID
    public String generateComplaintId() {
        return generateId("CMP");
    }

    // Ticket ID format: TKT + YEAR + 8-char UUID
    public String generateTicketId() {
        return generateId("TKT");
    }
}
