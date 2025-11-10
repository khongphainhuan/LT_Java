package com.pascs.controller;

import com.pascs.model.Feedback;
import com.pascs.model.Service;
import com.pascs.model.User;
import com.pascs.payload.request.FeedbackRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.service.FeedbackAnalysisService;
import com.pascs.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private com.pascs.repository.FeedbackRepository feedbackRepository;

    @Autowired
    private com.pascs.repository.UserRepository userRepository;

    @Autowired
    private com.pascs.repository.ServiceRepository serviceRepository;

    @Autowired
    private FeedbackAnalysisService feedbackAnalysisService;

    @PostMapping("")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("User", "id", userDetails.getId()));

            Service service = null;
            if (feedbackRequest.getServiceId() != null) {
                service = serviceRepository.findById(feedbackRequest.getServiceId())
                        .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Service", "id", feedbackRequest.getServiceId()));
            }

            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setService(service);
            feedback.setRating(feedbackRequest.getRating());
            feedback.setComment(feedbackRequest.getComment());

            feedbackRepository.save(feedback);
            return ResponseEntity.ok(new MessageResponse("Feedback submitted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getFeedbackStatistics() {
        try {
            FeedbackAnalysisService.OverallFeedbackStats stats = feedbackAnalysisService.getOverallFeedbackStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/service/{serviceId}/rating")
    public ResponseEntity<?> getServiceRating(@PathVariable Long serviceId) {
        try {
            FeedbackAnalysisService.ServiceRating rating = feedbackAnalysisService.calculateServiceRating(serviceId);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Feedback>> getRecentFeedback(
            @RequestParam(defaultValue = "10") int limit) {
        List<Feedback> feedbacks = feedbackAnalysisService.getRecentFeedback(limit);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Feedback>> getFeedbackByService(@PathVariable Long serviceId) {
        List<Feedback> feedbacks = feedbackRepository.findByServiceId(serviceId);
        return ResponseEntity.ok(feedbacks);
    }
}