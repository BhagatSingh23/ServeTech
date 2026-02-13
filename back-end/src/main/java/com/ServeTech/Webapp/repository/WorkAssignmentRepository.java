package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// This repository handles all database operations for work assignments
// This repository will also be used for WorkerDashBoard
@Repository
public interface WorkAssignmentRepository extends JpaRepository<WorkAssignment, Long> {

    // ========== Basic Queries ==========

    // Assignment ID for each work assignment is unique
    Optional<WorkAssignment> findByAssignmentId(String assignmentId);

    // ========== Worker Dashboard Queries ==========

    // Find all assignments for a specific worker
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.worker.id = :workerId ORDER BY wa.createdAt DESC")
    List<WorkAssignment> findAllByWorkerId(@Param("workerId") Long workerId);

    //  Find completed assignments for worker (for "Previous Bookings"section)
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.progressStatus = 'COMPLETED' ORDER BY wa.completedAt DESC")
    List<WorkAssignment> findCompletedAssignmentsByWorkerId(@Param("workerId") Long workerId);

    // Find in-progress assignments for worker (for "Current Bookings"section)
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.progressStatus = 'IN_PROGRESS' ORDER BY wa.startDate ASC")
    List<WorkAssignment> findInProgressAssignmentsByWorkerId(@Param("workerId") Long workerId);

    // Find upcoming assignments for worker (for "Upcoming Bookings"section) 'if any'
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.progressStatus = 'NOT_STARTED' ORDER BY wa.startDate ASC")
    List<WorkAssignment> findUpcomingAssignmentsByWorkerId(@Param("workerId") Long workerId);

    // Count completed jobs for a worker
    @Query("SELECT COUNT(wa) FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.progressStatus = 'COMPLETED'")
    Integer countCompletedJobsByWorkerId(@Param("workerId") Long workerId);

    // Inprogress jobs for a worker
    @Query("SELECT COUNT(wa) FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.progressStatus = 'IN_PROGRESS'")
    Integer countInProgressJobsByWorkerId(@Param("workerId") Long workerId);

    // Calculate total earnings for worker
    @Query("SELECT COALESCE(SUM(wa.amountPaid), 0.0) FROM WorkAssignment wa " +
            "WHERE wa.worker.id = :workerId")
    Double calculateTotalEarningsByWorkerId(@Param("workerId") Long workerId);

    // Track any pending payment for the worker
    @Query("SELECT COALESCE(SUM(wa.amountPending), 0.0) FROM WorkAssignment wa " +
            "WHERE wa.worker.id = :workerId AND wa.paymentStatus != 'PAID'")
    Double calculatePendingPaymentsByWorkerId(@Param("workerId") Long workerId);

    // Get average rating for worker from the client's perspective
    @Query("SELECT AVG(wa.clientRating) FROM WorkAssignment wa " +
            "WHERE wa.worker.id = :workerId AND wa.clientRating IS NOT NULL")
    Double getAverageRatingByWorkerId(@Param("workerId") Long workerId);

    // Total ratings received by worker
    @Query("SELECT COUNT(wa) FROM WorkAssignment wa WHERE wa.worker.id = :workerId " +
            "AND wa.clientRating IS NOT NULL")
    Integer countTotalRatingsByWorkerId(@Param("workerId") Long workerId);

    // ========== Client Dashboard Queries ==========

    // Find all assignments for a specific client
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.client.id = :clientId ORDER BY wa.createdAt DESC")
    List<WorkAssignment> findAllByClientId(@Param("clientId") Long clientId);

    // Find assignments by client ID and status
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.client.id = :clientId " +
            "AND wa.progressStatus = :status ORDER BY wa.createdAt DESC")
    List<WorkAssignment> findByClientIdAndStatus(@Param("clientId") Long clientId,
                                                 @Param("status") WorkProgressStatus status);

    // ========== Payment Queries ==========

    // Find assignments by payment status
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.paymentStatus = :status " +
            "ORDER BY wa.actualEndDate ASC")
    List<WorkAssignment> findByPaymentStatus(@Param("status") PaymentStatus status);

    // Find overdue payments
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.progressStatus = 'COMPLETED' " +
            "AND wa.paymentStatus != 'PAID' AND wa.actualEndDate < :date")
    List<WorkAssignment> findOverduePayments(@Param("date") LocalDate date);

    // ========== Admin/Analytics Queries ==========

    // Find assignments by progress status
    List<WorkAssignment> findByProgressStatus(WorkProgressStatus status);

    // Find assignments by date range
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.startDate >= :startDate " +
            "AND wa.startDate <= :endDate ORDER BY wa.startDate ASC")
    List<WorkAssignment> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Find assignments by work request ID
    @Query("SELECT wa FROM WorkAssignment wa WHERE wa.workRequest.id = :workRequestId")
    List<WorkAssignment> findByWorkRequestId(@Param("workRequestId") Long workRequestId);

    // Count total assignments
    @Query("SELECT COUNT(wa) FROM WorkAssignment wa")
    Long countTotalAssignments();

    // Get total earnings from completed assignments
    @Query("SELECT COALESCE(SUM(wa.totalAmount), 0.0) FROM WorkAssignment wa " +
            "WHERE wa.progressStatus = 'COMPLETED'")
    Double calculateTotalPlatformEarnings();
}
