package com.ServeTech.Webapp.dto.request;

import jakarta.validation.constraints.NotBlank;

// Class to store login request data
public class LoginRequest {

    @NotBlank(message = "Username or phone number is required")
    private String usernameOrPhone; // Can be username or phone number

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String usernameOrPhone, String password) {
        this.usernameOrPhone = usernameOrPhone;
        this.password = password;
    }

    // Getters and Setters
    public String getUsernameOrPhone() {
        return usernameOrPhone;
    }

    public void setUsernameOrPhone(String usernameOrPhone) {
        this.usernameOrPhone = usernameOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
