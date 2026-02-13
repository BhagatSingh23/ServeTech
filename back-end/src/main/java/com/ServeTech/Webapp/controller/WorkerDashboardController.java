package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.response.WorkerDashboardDTO;
import com.ServeTech.Webapp.dto.response.WorkerDashboardSummaryDTO;
import com.ServeTech.Webapp.service.WorkerDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/worker/dashboard")
@PreAuthorize("hasRole('WORKER')")
public class WorkerDashboardController {

    // Constructor Injection
    private final WorkerDashboardService workerDashboardService;

    public WorkerDashboardController(WorkerDashboardService workerDashboardService) {
        this.workerDashboardService = workerDashboardService;
    }

    // get worker dashboard summary data
    @GetMapping
    public ResponseEntity<WorkerDashboardSummaryDTO> getWorkerDashboard(Authentication authentication) {
        try {
            // Get worker ID from authenticated user
            Long workerId = getUserIdFromAuthentication(authentication);

            WorkerDashboardSummaryDTO dashboard = workerDashboardService.getWorkerDashboard(workerId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get completed bookings
    @GetMapping("/completed")
    public ResponseEntity<List<WorkerDashboardDTO>> getCompletedBookings(Authentication authentication) {
        try {
            Long workerId = getUserIdFromAuthentication(authentication);
            List<WorkerDashboardDTO> completedBookings =
                    workerDashboardService.getCompletedBookings(workerId);
            return ResponseEntity.ok(completedBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get current bookings (in progress)
    @GetMapping("/current")
    public ResponseEntity<List<WorkerDashboardDTO>> getCurrentBookings(Authentication authentication) {
        try {
            Long workerId = getUserIdFromAuthentication(authentication);
            List<WorkerDashboardDTO> currentBookings =
                    workerDashboardService.getCurrentBookings(workerId);
            return ResponseEntity.ok(currentBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get upcoming bookings (not yet started)
    @GetMapping("/upcoming")
    public ResponseEntity<List<WorkerDashboardDTO>> getUpcomingBookings(Authentication authentication) {
        try {
            Long workerId = getUserIdFromAuthentication(authentication);
            List<WorkerDashboardDTO> upcomingBookings =
                    workerDashboardService.getUpcomingBookings(workerId);
            return ResponseEntity.ok(upcomingBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all the bookings for a worker
    @GetMapping("/all")
    public ResponseEntity<List<WorkerDashboardDTO>> getAllBookings(Authentication authentication) {
        try {
            Long workerId = getUserIdFromAuthentication(authentication);
            List<WorkerDashboardDTO> allBookings =
                    workerDashboardService.getAllBookings(workerId);
            return ResponseEntity.ok(allBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to extract user ID from authentication object
    // Need to modify this method based on your authentication implementation
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Option 1: If you store User object in principal
        // User user = (User) authentication.getPrincipal();
        // return user.getId();

        // Option 2: If you store UserDetails with username
        // String username = authentication.getName();
        // return userService.findByUsername(username).getId();

        // Option 3: If you store userId directly
        // return Long.parseLong(authentication.getName());

        // Placeholder - replace with actual implementation
        return 1L; // TODO: Implement proper user ID extraction
    }
}
