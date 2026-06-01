package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.Payment;
import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPayeeIdOrderByCreatedAtDesc(Long payeeId);
    List<Payment> findByPayerIdOrderByCreatedAtDesc(Long payerId);
    List<Payment> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.payee.id = :workerId AND p.paymentStatus = 'PAID'")
    Double getTotalEarningsByWorker(@Param("workerId") Long workerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.payer.id = :clientId AND p.paymentStatus = 'PAID'")
    Double getTotalSpendingByClient(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.payee.id = :workerId AND p.paymentStatus = 'PENDING'")
    Double getPendingEarningsByWorker(@Param("workerId") Long workerId);
}
