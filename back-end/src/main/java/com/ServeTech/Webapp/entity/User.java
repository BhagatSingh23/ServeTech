package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.AccountStatus;
import com.ServeTech.Webapp.entity.enums.GenderType;
import com.ServeTech.Webapp.entity.enums.RoleType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Main db table for users to store their personal information
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone_number"),
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "unique_user_id")
        },
        indexes = {
                @Index(name = "idx_phone", columnList = "phone_number"),
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_status", columnList = "account_status")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // System-generated unique ID
    @Column(nullable = false, unique = true, length = 20, name = "unique_user_id")
    private String uniqueUserId;

    // Username format: FirstChar + LastChar + Last4Digits
    @Column(nullable = false, unique = true, length = 20)
    private String username;

    // ---------- Personal Information ----------
    @Column(nullable = false, length = 100, name = "first_name")
    private String firstName;

    @Column(nullable = false, length = 100, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GenderType genderType;

    // ---------- Contact & Location ----------
    @Column(nullable = false, length = 10, name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String pincode;

    // Many users can belong to one location
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private PincodeLocation location;

    // ---------- Authentication & Security ----------
    // Store BCrypt hashed password, never plain text
    @Column(nullable = false)
    private String password;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, name = "account_status")
    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

    // ---------- Roles (Many-to-Many) ----------
    // User can have multiple roles (though typically one)
    // Creates join table: user_roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // ---------- Profile ----------
    @Column(length = 500, name = "profile_image_url")
    private String profileImageUrl;

    // ---------- Audit Timestamps ----------
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Automatically set timestamps before saving
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    // Automatically update timestamp before updating
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public User() {
        this.roles = new HashSet<>();
    }

    public User(String firstName, String lastName, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.roles = new HashSet<>();
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public PincodeLocation getLocation() {
        return location;
    }

    public void setLocation(PincodeLocation location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Helper Methods

    // Add a role to this user
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Remove a role from this user
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // Check if user has a specific role
    public boolean hasRole(Enum<RoleType> roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    // Check if the user is active worker (not deleted)
    public boolean isWorker() {
        return hasRole(RoleType.ROLE_WORKER);
    }

    // Check if user is a client
    public boolean isClient() {
        return hasRole(RoleType.ROLE_CLIENT);
    }

    // Check if user is an admin
    public boolean isAdmin() {
        return hasRole(RoleType.ROLE_ADMIN);
    }

    // Method to get full name of user
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Custom toString method
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uniqueUserId='" + uniqueUserId + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", accountStatus=" + accountStatus +
                '}';
    }
}