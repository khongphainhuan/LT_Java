package com.pascs.repository;

import com.pascs.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByServiceId(Long serviceId);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByRating(Integer rating);
    
    // THÊM CÁC METHOD MỚI
    @Query("SELECT f FROM Feedback f ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedbacks(@Param("limit") int limit);
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE DATE(f.createdAt) = CURRENT_DATE")
    long countRecentFeedbacks();
    
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double findAverageRating();
    
    @Query("SELECT f.rating, COUNT(f) FROM Feedback f WHERE f.service.id = :serviceId GROUP BY f.rating")
    List<Object[]> findRatingDistributionByServiceId(@Param("serviceId") Long serviceId);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.service.id = :serviceId")
    Double findAverageRatingByServiceId(@Param("serviceId") Long serviceId);
}