package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {

    Optional<WorkerProfile> findByUserId(Long userId);

    List<WorkerProfile> findByIsVerifiedFalse();
}