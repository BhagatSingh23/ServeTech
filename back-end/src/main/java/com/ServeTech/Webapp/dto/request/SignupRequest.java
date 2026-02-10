package com.ServeTech.Webapp.dto.request;

import com.ServeTech.Webapp.entity.enums.GenderType;
import com.ServeTech.Webapp.entity.enums.RoleType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


// This class will assist in storing data related to signup
public class SignupRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Enum<GenderType> gender;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9]\\d{5}$", message = "Invalid pincode")
    private String pincode;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(ROLE_WORKER|ROLE_CLIENT)$", message = "Role must be ROLE_WORKER or ROLE_CLIENT")
    private Enum<RoleType> role; // ROLE_WORKER or ROLE_CLIENT

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    // Constructors
    public SignupRequest() {
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public GenderType getGender() {
        return (GenderType) gender;
    }

    public void setGender(GenderType genderType) {
        this.gender = genderType;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public @NotBlank(message = "Role is required") @Pattern(regexp = "^(ROLE_WORKER|ROLE_CLIENT)$", message = "Role must be ROLE_WORKER or ROLE_CLIENT") Enum<RoleType> getRole() {
        return role;
    }

    public void setRole(@NotBlank @Pattern(regexp = "") Enum<RoleType> role) {
        this.role = role;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
