package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.Complaint;
import com.ServeTech.Webapp.entity.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findByComplainantIdOrderByFiledAtDesc(Long complainantId);
    List<Complaint> findByStatusOrderByFiledAtDesc(ComplaintStatus status);
    List<Complaint> findByAssignedAdminIdOrderByFiledAtDesc(Long adminId);
    long countByStatus(ComplaintStatus status);
    List<Complaint> findAllByOrderByFiledAtDesc();
}
