package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone_number")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- Personal Info ----------
    @Column(nullable = false, length = 100, name = "first_name")
    private String firstName;

    @Column(nullable = false, length = 100, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "gender")
    private Gender gender;

    // ---------- Location ----------
    @Column(nullable = false, length = 6, name = "pincode")
    private String pincode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private PincodeLocation location;

    // ---------- Auth ----------
    @Column(nullable = false, length = 10, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, name = "password")
    private String password;

    // ---------- Status ----------
    private boolean enabled = true;
    private boolean phoneVerified = false;

    // ---------- Roles ----------
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // ---------- Skills (only for workers) ----------
    @ManyToMany
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    // ---------- Audit ----------
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
