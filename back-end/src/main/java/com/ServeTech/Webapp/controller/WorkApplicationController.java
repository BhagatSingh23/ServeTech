package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.CreateApplicationRequest;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.ApplicationResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.WorkApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class WorkApplicationController {

    private final WorkApplicationService workApplicationService;

    public WorkApplicationController(WorkApplicationService workApplicationService) {
        this.workApplicationService = workApplicationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> apply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateApplicationRequest request) {
        ApplicationResponse response = workApplicationService.applyForJob(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Application submitted", response));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<ApiResponse> getMyApplications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<ApplicationResponse> applications = workApplicationService.getMyApplications(
                userPrincipal.getId(), status);
        return ResponseEntity.ok(new ApiResponse(true, "Applications fetched", applications));
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse> withdrawApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        ApplicationResponse response = workApplicationService.withdrawApplication(
                userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Application withdrawn", response));
    }

    @GetMapping("/work-request/{id}")
    public ResponseEntity<ApiResponse> getApplicationsForWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        List<ApplicationResponse> applications = workApplicationService.getApplicationsForWorkRequest(
                userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Applications fetched", applications));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse> acceptApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        ApplicationResponse response = workApplicationService.acceptApplication(
                userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Application accepted", response));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> rejectApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        ApplicationResponse response = workApplicationService.rejectApplication(
                userPrincipal.getId(), id, reason);
        return ResponseEntity.ok(new ApiResponse(true, "Application rejected", response));
    }
}
