package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.SubmitRatingRequest;
import com.ServeTech.Webapp.dto.response.RatingResponse;
import com.ServeTech.Webapp.entity.Rating;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkAssignment;
import com.ServeTech.Webapp.entity.enums.WorkProgressStatus;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.RatingRepository;
import com.ServeTech.Webapp.repository.UserRepository;
import com.ServeTech.Webapp.repository.WorkAssignmentRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final WorkAssignmentRepository workAssignmentRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;

    public RatingService(RatingRepository ratingRepository,
                         WorkAssignmentRepository workAssignmentRepository,
                         UserRepository userRepository,
                         WorkerProfileRepository workerProfileRepository) {
        this.ratingRepository = ratingRepository;
        this.workAssignmentRepository = workAssignmentRepository;
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
    }

    @Transactional
    public RatingResponse submitRating(Long raterId, SubmitRatingRequest request) {
        WorkAssignment assignment = workAssignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new CustomException("Assignment not found", HttpStatus.NOT_FOUND));

        if (assignment.getProgressStatus() != WorkProgressStatus.COMPLETED) {
            throw new CustomException("Can only rate completed assignments", HttpStatus.BAD_REQUEST);
        }

        // Determine who is rating whom
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        User ratee;
        boolean isClientRating = assignment.getClient().getId().equals(raterId);
        boolean isWorkerRating = assignment.getWorker().getId().equals(raterId);

        if (!isClientRating && !isWorkerRating) {
            throw new CustomException("You are not part of this assignment", HttpStatus.FORBIDDEN);
        }

        if (ratingRepository.existsByAssignmentIdAndRaterId(request.getAssignmentId(), raterId)) {
            throw new CustomException("You have already rated this assignment", HttpStatus.CONFLICT);
        }

        ratee = isClientRating ? assignment.getWorker() : assignment.getClient();

        Rating rating = new Rating();
        rating.setAssignment(assignment);
        rating.setRater(rater);
        rating.setRatee(ratee);
        rating.setRating(request.getRating());
        rating.setReview(request.getReview());
        rating.setSkillRating(request.getSkillRating());
        rating.setProfessionalismRating(request.getProfessionalismRating());
        rating.setPunctualityRating(request.getPunctualityRating());

        Rating saved = ratingRepository.save(rating);

        // Update assignment ratings
        if (isClientRating) {
            assignment.setClientRating(request.getRating());
            assignment.setClientReview(request.getReview());
        } else {
            assignment.setWorkerRating(request.getRating());
            assignment.setWorkerReview(request.getReview());
        }
        workAssignmentRepository.save(assignment);

        // Update ratee's average rating in profile
        if (isClientRating) {
            workerProfileRepository.findByUserId(ratee.getId()).ifPresent(profile -> {
                Double avgRating = ratingRepository.getAverageRatingByUser(ratee.getId());
                Long totalRatings = ratingRepository.getTotalRatingsByUser(ratee.getId());
                profile.setAverageRating(avgRating);
                profile.setTotalRatings(totalRatings.intValue());
                workerProfileRepository.save(profile);
            });
        }

        return RatingResponse.fromEntity(saved);
    }

    public List<RatingResponse> getMyRatings(Long userId) {
        return ratingRepository.findByRateeIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RatingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RatingResponse> getGivenRatings(Long userId) {
        return ratingRepository.findByRaterIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RatingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RatingResponse> getUserRatings(Long userId) {
        return ratingRepository.findByRateeIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RatingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RatingResponse> getAssignmentRatings(Long assignmentId) {
        return ratingRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId)
                .stream()
                .map(RatingResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
