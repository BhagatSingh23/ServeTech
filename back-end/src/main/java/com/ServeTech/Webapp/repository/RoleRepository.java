package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.Role;
import com.ServeTech.Webapp.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // FIX: The parameter must be RoleType, NOT String
    Optional<Role> findByName(RoleType name);
}