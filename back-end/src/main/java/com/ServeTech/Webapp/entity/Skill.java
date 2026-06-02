package com.ServeTech.Webapp.entity;

import com.ServeTech.Webapp.entity.enums.SkillType;
import jakarta.persistence.*;

// Skill entity - Stores skills of the workers
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 50)
    private SkillType name;

    // Flag to enable/disable skills
    @Column(nullable = false)
    private boolean active = true;

    // Constructors
    public Skill() {
    }

    public Skill(SkillType name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SkillType getName() {
        return name;
    }

    public void setName(SkillType name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
