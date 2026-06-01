package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRateeIdOrderByCreatedAtDesc(Long rateeId);
    List<Rating> findByRaterIdOrderByCreatedAtDesc(Long raterId);
    List<Rating> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);
    boolean existsByAssignmentIdAndRaterId(Long assignmentId, Long raterId);
    List<Rating> findTop5ByRateeIdOrderByCreatedAtDesc(Long rateeId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Rating r WHERE r.ratee.id = :userId")
    Double getAverageRatingByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratee.id = :userId")
    Long getTotalRatingsByUser(@Param("userId") Long userId);
}
