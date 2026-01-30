package com.ServeTech.Webapp.dto.response;

import com.ServeTech.Webapp.entity.Role;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.enums.AccountStatus;
import com.ServeTech.Webapp.entity.enums.GenderType;
import com.ServeTech.Webapp.entity.enums.RoleType;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

// This is a response class for User entity
// This will be used to return user data to the client
// This class can later be used to return user data to the admin
// This will also assist later development the webapp when advancements are made to the webapp
public class UserResponse {

    private Long id;
    private String uniqueUserId;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private GenderType genderType;
    private String pincode;
    private String block;
    private String district;
    private String state;
    private AccountStatus accountStatus;
    private boolean phoneVerified;
    private Set<Enum<RoleType>> roles;
    private String profileImageUrl;

    // Constructors
    public UserResponse() {
    }

    // Convert User entity to UserResponse
    public UserResponse(User user) {
        this.id = user.getId();
        this.uniqueUserId = user.getUniqueUserId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.dateOfBirth = user.getDateOfBirth();
        this.genderType = user.getGender();
        this.pincode = user.getPincode();
        if (user.getBlock() != null && user.getDistrict() != null && user.getState() != null) {
            this.block = user.getBlock();
            this.district = user.getDistrict();
            this.state = user.getState();
        }
        this.accountStatus = user.getAccountStatus();
        this.phoneVerified = user.isPhoneVerified();
        this.roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        this.profileImageUrl = user.getProfileImageUrl();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public GenderType getGender() {
        return genderType;
    }

    public void setGender(GenderType genderType) {
        this.genderType = genderType;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Set<Enum<RoleType>> getRoles() {
        return roles;
    }

    public void setRoles(Set<Enum<RoleType>> roles) {
        this.roles = roles;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
