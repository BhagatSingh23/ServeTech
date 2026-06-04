package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.WorkRequestResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.WorkRequestPublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class WorkRequestPublicController {

    private final WorkRequestPublicService workRequestPublicService;

    public WorkRequestPublicController(WorkRequestPublicService workRequestPublicService) {
        this.workRequestPublicService = workRequestPublicService;
    }

    @GetMapping("/browse")
    public ResponseEntity<ApiResponse> browseJobs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Boolean urgent) {
        List<WorkRequestResponse> jobs = workRequestPublicService.browseJobs(
                userPrincipal.getId(), pincode, skillIds, urgent);
        return ResponseEntity.ok(new ApiResponse(true, "Jobs fetched", jobs));
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse> getRecommendedJobs(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WorkRequestResponse> jobs = workRequestPublicService.getRecommendedJobs(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Recommended jobs fetched", jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getJobDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        WorkRequestResponse job = workRequestPublicService.getJobDetails(id, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Job details fetched", job));
    }
}
