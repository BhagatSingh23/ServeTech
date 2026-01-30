package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// This db table stores user login history of the user
@Entity
@Table(
        name = "user_login_history",
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_login_time", columnList = "login_time")
        }
)
public class UserLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(length = 50, name = "ip_address")
    private String ipAddress;

    @Column(length = 200, name = "user_agent")
    private String userAgent; // Browser/device info

    @Column(length = 20, name = "login_status")
    private String loginStatus; // SUCCESS, FAILED

    // Constructors
    public UserLoginHistory() {
    }

    public UserLoginHistory(User user, String loginStatus) {
        this.user = user;
        this.loginStatus = loginStatus;
    }

    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    // Helper Methods

    // log the Logout time of the user
    public void logout() {
        this.logoutTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "UserLoginHistory{" +
                "id=" + id +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                ", loginStatus='" + loginStatus + '\'' +
                '}';
    }
}