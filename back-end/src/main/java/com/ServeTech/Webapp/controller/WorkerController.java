package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.UpdateWorkerProfileRequest;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.ApplicationResponse;
import com.ServeTech.Webapp.dto.response.WorkerProfileResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.WorkApplicationService;
import com.ServeTech.Webapp.service.WorkerDashboardService;
import com.ServeTech.Webapp.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    private final WorkerService workerService;
    private final WorkerDashboardService workerDashboardService;
    private final WorkApplicationService workApplicationService;

    public WorkerController(WorkerService workerService,
                           WorkerDashboardService workerDashboardService,
                           WorkApplicationService workApplicationService) {
        this.workerService = workerService;
        this.workerDashboardService = workerDashboardService;
        this.workApplicationService = workApplicationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getWorkerDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var dashboard = workerDashboardService.getWorkerDashboard(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Worker dashboard fetched", dashboard));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        WorkerProfileResponse profile = workerService.getProfile(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Profile fetched", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateWorkerProfileRequest request) {
        WorkerProfileResponse profile = workerService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated", profile));
    }

    @PatchMapping("/profile/availability")
    public ResponseEntity<ApiResponse> toggleAvailability(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        WorkerProfileResponse profile = workerService.toggleAvailability(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Availability updated", profile));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse> getApplications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<ApplicationResponse> applications = workApplicationService.getMyApplications(
                userPrincipal.getId(), status);
        return ResponseEntity.ok(new ApiResponse(true, "Applications fetched", applications));
    }
}
