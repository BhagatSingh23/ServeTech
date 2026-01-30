package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.RoleType;
import jakarta.persistence.*;

// Stores user roles
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role name: ROLE_ADMIN, ROLE_CLIENT, ROLE_WORKER
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 50)
    private RoleType name;

    // Constructors
    public Role() {
    }

    public Role(RoleType name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Enum<RoleType> getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
