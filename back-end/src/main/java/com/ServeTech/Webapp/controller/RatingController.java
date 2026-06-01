package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.SubmitRatingRequest;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.RatingResponse;
import com.ServeTech.Webapp.security.UserPrincipal;
import com.ServeTech.Webapp.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> submitRating(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SubmitRatingRequest request) {
        RatingResponse response = ratingService.submitRating(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Rating submitted", response));
    }

    @GetMapping("/my-ratings")
    public ResponseEntity<ApiResponse> getMyRatings(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RatingResponse> ratings = ratingService.getMyRatings(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Ratings fetched", ratings));
    }

    @GetMapping("/given-by-me")
    public ResponseEntity<ApiResponse> getGivenRatings(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RatingResponse> ratings = ratingService.getGivenRatings(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Given ratings fetched", ratings));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserRatings(@PathVariable Long userId) {
        List<RatingResponse> ratings = ratingService.getUserRatings(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User ratings fetched", ratings));
    }

    @GetMapping("/assignment/{id}")
    public ResponseEntity<ApiResponse> getAssignmentRatings(@PathVariable Long id) {
        List<RatingResponse> ratings = ratingService.getAssignmentRatings(id);
        return ResponseEntity.ok(new ApiResponse(true, "Assignment ratings fetched", ratings));
    }
}
