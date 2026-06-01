package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.RecordPaymentRequest;
import com.ServeTech.Webapp.dto.response.PaymentResponse;
import com.ServeTech.Webapp.entity.Payment;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.enums.PaymentStatus;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.PaymentRepository;
import com.ServeTech.Webapp.repository.UserRepository;
import com.ServeTech.Webapp.repository.WorkAssignmentRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final UniqueIdGenerator uniqueIdGenerator;

    public PaymentService(PaymentRepository paymentRepository,
                          WorkAssignmentRepository workAssignmentRepository,
                          UserRepository userRepository,
                          WorkerProfileRepository workerProfileRepository,
                          UniqueIdGenerator uniqueIdGenerator) {
        this.paymentRepository = paymentRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @Transactional
    public PaymentResponse recordPayment(Long payerId, RecordPaymentRequest request) {
        WorkAssignment assignment = workAssignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new CustomException("Assignment not found", HttpStatus.NOT_FOUND));

        if (!assignment.getClient().getId().equals(payerId)) {
            throw new CustomException("Access denied: you are not the client for this assignment", HttpStatus.FORBIDDEN);
        }

        Payment payment = new Payment();
        payment.setTransactionId(uniqueIdGenerator.generateTransactionId());
        payment.setAssignment(assignment);
        payment.setPayer(assignment.getClient());
        payment.setPayee(assignment.getWorker());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentReference(request.getPaymentReference());
        payment.setNotes(request.getPaymentNotes());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // Update assignment payment tracking
        assignment.addPayment(request.getAmount());
        workAssignmentRepository.save(assignment);

        // Update worker profile earnings
        workerProfileRepository.findByUserId(assignment.getWorker().getId()).ifPresent(profile -> {
            profile.addEarnings(request.getAmount());
            workerProfileRepository.save(profile);
        });

        return PaymentResponse.fromEntity(saved);
    }

    public List<PaymentResponse> getPaymentsForAssignment(Long assignmentId) {
        return paymentRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getWorkerPayments(Long workerId) {
        return paymentRepository.findByPayeeIdOrderByCreatedAtDesc(workerId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getClientPayments(Long clientId) {
        return paymentRepository.findByPayerIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
