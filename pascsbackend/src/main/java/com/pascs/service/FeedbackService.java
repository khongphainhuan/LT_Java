package com.pascs.service;

import com.pascs.payload.request.FeedbackRequest;
import com.pascs.payload.response.FeedbackResponse;
import com.pascs.model.Feedback;
import com.pascs.model.Application;
import com.pascs.model.User;
import com.pascs.exception.ResourceNotFoundException;
import com.pascs.repository.FeedbackRepository;
import com.pascs.repository.ApplicationRepository;
import com.pascs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Feedback Management
 * @author Dũng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        log.info("Submitting feedback from user: {}", request.getUserId());
        
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        
        if (request.getApplicationId() != null) {
            Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", request.getApplicationId()));
            feedback.setApplication(application);
        }
        
        feedback = feedbackRepository.save(feedback);
        
        log.info("Feedback submitted: ID {}, Rating: {}", feedback.getId(), feedback.getRating());
        return mapToResponse(feedback);
    }
    
    public FeedbackResponse getById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        return mapToResponse(feedback);
    }
    
    public List<FeedbackResponse> getUserFeedbacks(Long userId) {
        return feedbackRepository.findByUserIdOrderByFeedbackDateDesc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<FeedbackResponse> getApplicationFeedbacks(Long applicationId) {
        return feedbackRepository.findByApplicationIdOrderByFeedbackDateDesc(applicationId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public Double getAverageRating() {
        Double avg = feedbackRepository.getAverageRating();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
    
    public List<FeedbackResponse> getNegativeFeedbacks() {
        return feedbackRepository.findNegativeFeedbacks()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public Long getPositiveFeedbackCount() {
        return feedbackRepository.countPositiveFeedbacks();
    }
    
    private FeedbackResponse mapToResponse(Feedback feedback) {
        return FeedbackResponse.builder()
            .id(feedback.getId())
            .userName(feedback.getUser() != null ? feedback.getUser().getFullName() : "Guest")
            .userId(feedback.getUser() != null ? feedback.getUser().getId() : null)
            .applicationId(feedback.getApplication() != null ? 
                          feedback.getApplication().getId() : null)
            .rating(feedback.getRating())
            .comment(feedback.getComment())
            .feedbackDate(feedback.getFeedbackDate())
            .status(feedback.getStatus())
            .build();
    }
}
