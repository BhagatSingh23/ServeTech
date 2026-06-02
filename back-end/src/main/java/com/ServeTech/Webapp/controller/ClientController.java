package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.CreateWorkRequestDTO;
import com.ServeTech.Webapp.dto.request.UpdateWorkRequestDTO;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.ApplicationResponse;
import com.ServeTech.Webapp.dto.response.ClientDashboardResponse;
import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ClientDashboardResponse dashboard = clientService.getDashboard(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Client dashboard fetched", dashboard));
    }

    @PostMapping("/work-requests")
    public ResponseEntity<ApiResponse> createWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateWorkRequestDTO request) {
        WorkRequestResponse response = clientService.createWorkRequest(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Work request created", response));
    }

    @GetMapping("/work-requests")
    public ResponseEntity<ApiResponse> getWorkRequests(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<WorkRequestResponse> requests = clientService.getWorkRequests(userPrincipal.getId(), status);
        return ResponseEntity.ok(new ApiResponse(true, "Work requests fetched", requests));
    }

    @GetMapping("/work-requests/{id}")
    public ResponseEntity<ApiResponse> getWorkRequestById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        WorkRequestResponse response = clientService.getWorkRequestById(userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Work request fetched", response));
    }

    @PutMapping("/work-requests/{id}")
    public ResponseEntity<ApiResponse> updateWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkRequestDTO request) {
        WorkRequestResponse response = clientService.updateWorkRequest(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Work request updated", response));
    }

    @DeleteMapping("/work-requests/{id}")
    public ResponseEntity<ApiResponse> deleteWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        clientService.deleteWorkRequest(userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Work request deleted"));
    }

    @PatchMapping("/work-requests/{id}/close")
    public ResponseEntity<ApiResponse> closeWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        WorkRequestResponse response = clientService.closeWorkRequest(userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Work request closed", response));
    }

    @GetMapping("/work-requests/{id}/applications")
    public ResponseEntity<ApiResponse> getApplicationsForWorkRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        List<ApplicationResponse> applications = clientService.getApplicationsForWorkRequest(
                userPrincipal.getId(), id);
        return ResponseEntity.ok(new ApiResponse(true, "Applications fetched", applications));
    }

    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse> getClientAssignments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<com.ServeTech.Webapp.dto.response.ClientAssignmentDTO> assignments = clientService.getClientAssignments(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Assignments fetched", assignments));
    }
}
