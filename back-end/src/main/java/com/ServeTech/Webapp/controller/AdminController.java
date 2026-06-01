package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.response.AdminDashboardResponse;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.ComplaintResponse;
import com.ServeTech.Webapp.dto.response.UserResponse;
import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        AdminDashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(new ApiResponse(true, "Admin dashboard fetched", dashboard));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        List<UserResponse> users = adminService.getAllUsers(role, status);
        return ResponseEntity.ok(new ApiResponse(true, "Users fetched", users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse(true, "User fetched", user));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("accountStatus");
        UserResponse user = adminService.updateUserStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "User status updated", user));
    }

    @PatchMapping("/workers/{id}/verify")
    public ResponseEntity<ApiResponse> verifyWorker(@PathVariable Long id) {
        adminService.verifyWorker(id);
        return ResponseEntity.ok(new ApiResponse(true, "Worker verified successfully"));
    }

    @PatchMapping("/workers/{id}/reject-verification")
    public ResponseEntity<ApiResponse> rejectVerification(@PathVariable Long id) {
        adminService.rejectVerification(id);
        return ResponseEntity.ok(new ApiResponse(true, "Worker verification rejected"));
    }

    @GetMapping("/work-requests")
    public ResponseEntity<ApiResponse> getAllWorkRequests(
            @RequestParam(required = false) String status) {
        List<WorkRequestResponse> requests = adminService.getAllWorkRequests(status);
        return ResponseEntity.ok(new ApiResponse(true, "Work requests fetched", requests));
    }

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse> getAllComplaints(
            @RequestParam(required = false) String status) {
        List<ComplaintResponse> complaints = adminService.getAllComplaints(status);
        return ResponseEntity.ok(new ApiResponse(true, "Complaints fetched", complaints));
    }

    @PatchMapping("/complaints/{id}/assign")
    public ResponseEntity<ApiResponse> assignComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ComplaintResponse complaint = adminService.assignComplaint(id, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Complaint assigned", complaint));
    }

    @PatchMapping("/complaints/{id}/resolve")
    public ResponseEntity<ApiResponse> resolveComplaint(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String resolution = body.get("resolution");
        ComplaintResponse complaint = adminService.resolveComplaint(id, resolution);
        return ResponseEntity.ok(new ApiResponse(true, "Complaint resolved", complaint));
    }
}
