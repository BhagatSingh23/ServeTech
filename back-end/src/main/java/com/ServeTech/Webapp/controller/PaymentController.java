package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.RecordPaymentRequest;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.PaymentResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> recordPayment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RecordPaymentRequest request) {
        PaymentResponse response = paymentService.recordPayment(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Payment recorded", response));
    }

    @GetMapping("/assignment/{id}")
    public ResponseEntity<ApiResponse> getPaymentsForAssignment(@PathVariable Long id) {
        List<PaymentResponse> payments = paymentService.getPaymentsForAssignment(id);
        return ResponseEntity.ok(new ApiResponse(true, "Payments fetched", payments));
    }

    @GetMapping("/worker/me")
    public ResponseEntity<ApiResponse> getWorkerPayments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<PaymentResponse> payments = paymentService.getWorkerPayments(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Worker payments fetched", payments));
    }

    @GetMapping("/client/me")
    public ResponseEntity<ApiResponse> getClientPayments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<PaymentResponse> payments = paymentService.getClientPayments(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Client payments fetched", payments));
    }
}
