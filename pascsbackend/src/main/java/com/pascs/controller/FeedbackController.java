package com.pascs.controller;

import com.pascs.payload.request.FeedbackRequest;
import com.pascs.payload.response.ApiResponse;
import com.pascs.payload.response.FeedbackResponse;
import com.pascs.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Feedback Management
 * @author Dũng
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    /**
     * Gửi phản hồi
     * POST /api/feedbacks/submit
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @Valid @RequestBody FeedbackRequest request) {
        
        log.info("API: Submit feedback from user {}", request.getUserId());
        FeedbackResponse response = feedbackService.submitFeedback(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Feedback submitted successfully", response));
    }
    
    /**
     * Lấy phản hồi theo ID
     * GET /api/feedbacks/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getById(
            @PathVariable Long id) {
        
        log.info("API: Get feedback by id {}", id);
        FeedbackResponse response = feedbackService.getById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy phản hồi của user
     * GET /api/feedbacks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getUserFeedbacks(
            @PathVariable Long userId) {
        
        log.info("API: Get feedbacks for user {}", userId);
        List<FeedbackResponse> response = feedbackService.getUserFeedbacks(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy phản hồi của hồ sơ
     * GET /api/feedbacks/application/{applicationId}
     */
    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getApplicationFeedbacks(
            @PathVariable Long applicationId) {
        
        log.info("API: Get feedbacks for application {}", applicationId);
        List<FeedbackResponse> response = feedbackService.getApplicationFeedbacks(applicationId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy điểm trung bình
     * GET /api/feedbacks/average-rating
     */
    @GetMapping("/average-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating() {
        
        log.info("API: Get average rating");
        Double rating = feedbackService.getAverageRating();
        
        return ResponseEntity.ok(ApiResponse.success(rating));
    }
    
    /**
     * Lấy phản hồi tiêu cực
     * GET /api/feedbacks/negative
     */
    @GetMapping("/negative")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getNegativeFeedbacks() {
        
        log.info("API: Get negative feedbacks");
        List<FeedbackResponse> response = feedbackService.getNegativeFeedbacks();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Đếm phản hồi tích cực
     * GET /api/feedbacks/positive-count
     */
    @GetMapping("/positive-count")
    public ResponseEntity<ApiResponse<Long>> getPositiveCount() {
        
        log.info("API: Get positive feedback count");
        Long count = feedbackService.getPositiveFeedbackCount();
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
