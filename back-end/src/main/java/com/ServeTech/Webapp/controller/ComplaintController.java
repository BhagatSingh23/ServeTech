package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.FileComplaintRequest;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.ComplaintResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> fileComplaint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody FileComplaintRequest request) {
        ComplaintResponse complaint = complaintService.fileComplaint(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Complaint filed successfully", complaint));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ComplaintResponse> complaints = complaintService.getMyComplaints(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Complaints fetched", complaints));
    }
}
