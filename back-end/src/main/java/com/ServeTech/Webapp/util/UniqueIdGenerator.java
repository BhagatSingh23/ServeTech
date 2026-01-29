package com.ServeTech.Webapp.util;

import org.springframework.stereotype.Component;
import java.time.Year;

// This class is used to generate unique IDs for users, work requests, work assignments and transactions
@Component
public class UniqueIdGenerator {

    private static long counter = 1;

    // This method is synchronized to ensure thread-safe operation
    // This will prevent multiple threads from generating the same unique ID at the same time
    public synchronized String generateUniqueUserId() {
        int currentYear = Year.now().getValue();
        String formattedCounter = String.format("%06d", counter);
        counter++;
        return currentYear + formattedCounter;
    }

    // Work request ID format: WR + YEAR + 6-digit number
    public synchronized String generateWorkRequestId() {
        int currentYear = Year.now().getValue();
        String formattedCounter = String.format("%06d", counter);
        counter++;
        return "WR" + currentYear + formattedCounter;
    }

    // Generate work assignment ID
    public synchronized String generateWorkAssignmentId() {
        int currentYear = Year.now().getValue();
        String formattedCounter = String.format("%06d", counter);
        counter++;
        return "WA" + currentYear + formattedCounter;
    }

    // Transaction ID format: TXN + YEAR + 6-digit number
    // This will help us identify transactions in the database
    // Keep track of the last generated transaction ID to ensure uniqueness
    public synchronized String generateTransactionId() {
        int currentYear = Year.now().getValue();
        String formattedCounter = String.format("%06d", counter);
        counter++;
        return "TXN" + currentYear + formattedCounter;
    }
}
