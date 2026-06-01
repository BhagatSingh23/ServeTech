package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.WorkRequest;
import com.ServeTech.Webapp.entity.enums.WorkRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRequestRepository extends JpaRepository<WorkRequest, Long> {
    Optional<WorkRequest> findByRequestId(String requestId);
    List<WorkRequest> findByClientIdOrderByCreatedAtDesc(Long clientId);
    List<WorkRequest> findByClientIdAndStatusOrderByCreatedAtDesc(Long clientId, WorkRequestStatus status);
    List<WorkRequest> findByStatusOrderByCreatedAtDesc(WorkRequestStatus status);
    List<WorkRequest> findByPincodeAndStatusOrderByCreatedAtDesc(String pincode, WorkRequestStatus status);

    @Query("SELECT wr FROM WorkRequest wr WHERE wr.status = 'OPEN' AND wr.pincode = :pincode ORDER BY wr.isUrgent DESC, wr.createdAt DESC")
    List<WorkRequest> findOpenRequestsByPincode(@Param("pincode") String pincode);

    @Query("SELECT wr FROM WorkRequest wr JOIN wr.requiredSkills rs WHERE wr.status = 'OPEN' AND wr.pincode = :pincode AND rs.id IN :skillIds ORDER BY wr.isUrgent DESC, wr.createdAt DESC")
    List<WorkRequest> findMatchingRequestsForWorker(@Param("pincode") String pincode, @Param("skillIds") List<Long> skillIds);

    @Query("SELECT wr FROM WorkRequest wr WHERE wr.status = 'OPEN' ORDER BY wr.isUrgent DESC, wr.createdAt DESC")
    List<WorkRequest> findAllOpenRequests();

    @Query("SELECT wr FROM WorkRequest wr WHERE wr.status = 'OPEN' AND wr.isUrgent = true ORDER BY wr.createdAt DESC")
    List<WorkRequest> findUrgentOpenRequests();

    long countByStatus(WorkRequestStatus status);
}
