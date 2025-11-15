package com.pascs.repository;

import com.pascs.model.Feedback;  
import com.pascs.model.Feedback.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * Repository for Feedback operations
 * @author Dũng
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByUserIdOrderByFeedbackDateDesc(Long userId);
    
    List<Feedback> findByApplicationIdOrderByFeedbackDateDesc(Long applicationId);
    
    List<Feedback> findByStatusOrderByFeedbackDateDesc(FeedbackStatus status);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();
    
    @Query("SELECT f.rating, COUNT(f) FROM Feedback f " +
           "GROUP BY f.rating ORDER BY f.rating DESC")
    List<Object[]> getRatingDistribution();
    
    @Query("SELECT f FROM Feedback f WHERE f.rating <= 2 " +
           "ORDER BY f.feedbackDate DESC")
    List<Feedback> findNegativeFeedbacks();
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.rating >= 4")
    Long countPositiveFeedbacks();
}
