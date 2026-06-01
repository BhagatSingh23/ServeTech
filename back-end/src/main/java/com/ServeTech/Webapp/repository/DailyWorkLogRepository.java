package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.DailyWorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyWorkLogRepository extends JpaRepository<DailyWorkLog, Long> {
    List<DailyWorkLog> findByAssignmentIdOrderByWorkDateDesc(Long assignmentId);
    Optional<DailyWorkLog> findByAssignmentIdAndWorkDate(Long assignmentId, LocalDate workDate);
}
