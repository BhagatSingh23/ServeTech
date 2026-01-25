package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;

// Stores user roles
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role name: ROLE_ADMIN, ROLE_CLIENT, ROLE_WORKER
    @Column(unique = true, nullable = false, length = 20)
    private String name;

    @Column(length = 200)
    private String description;

    // Constructors
    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
