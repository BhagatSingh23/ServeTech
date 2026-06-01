package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.WorkApplication;
import com.ServeTech.Webapp.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkApplicationRepository extends JpaRepository<WorkApplication, Long> {
    List<WorkApplication> findByWorkerIdOrderByAppliedAtDesc(Long workerId);
    List<WorkApplication> findByWorkerIdAndStatusOrderByAppliedAtDesc(Long workerId, ApplicationStatus status);
    List<WorkApplication> findByWorkRequestIdOrderByAppliedAtDesc(Long workRequestId);
    List<WorkApplication> findByWorkRequestIdAndStatusOrderByAppliedAtDesc(Long workRequestId, ApplicationStatus status);
    boolean existsByWorkerIdAndWorkRequestId(Long workerId, Long workRequestId);
    Optional<WorkApplication> findByWorkerIdAndWorkRequestId(Long workerId, Long workRequestId);
    List<WorkApplication> findTop5ByWorkerIdOrderByAppliedAtDesc(Long workerId);
    long countByWorkRequestIdAndStatus(Long workRequestId, ApplicationStatus status);
}
