package com.ServeTech.Webapp.util;

import org.springframework.stereotype.Component;

// This class is used to generate usernames from first and last names and unique IDs
@Component
public class UsernameGenerator {

    // Generate username from first and last names and unique ID
    public String generateUsername(String firstName, String lastName, String uniqueUserId) {
        // Get first character of first name
        char firstChar = firstName.toUpperCase().charAt(0);

        // Get first character of last name
        char lastChar = lastName.toUpperCase().charAt(0);

        // Get last 4 digits of unique ID
        String last4Digits = uniqueUserId.substring(uniqueUserId.length() - 4);

        return "" + firstChar + lastChar + last4Digits;
    }
}