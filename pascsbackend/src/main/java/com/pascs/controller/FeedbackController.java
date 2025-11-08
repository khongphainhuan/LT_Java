package com.pascs.controller;

import com.pascs.model.Feedback;
import com.pascs.model.Service;
import com.pascs.model.User;
import com.pascs.payload.request.FeedbackRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.exception.ResourceNotFoundException;
import com.pascs.repository.FeedbackRepository;
import com.pascs.repository.ServiceRepository;
import com.pascs.repository.UserRepository;
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
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    // Gửi phản hồi
    @PostMapping("")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        Service service = null;
        if (feedbackRequest.getServiceId() != null) {
            service = serviceRepository.findById(feedbackRequest.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", feedbackRequest.getServiceId()));
        }

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setService(service);
        feedback.setRating(feedbackRequest.getRating());
        feedback.setComment(feedbackRequest.getComment());

        feedbackRepository.save(feedback);
        return ResponseEntity.ok(new MessageResponse("Feedback submitted successfully!"));
    }

    // Lấy tất cả phản hồi (Admin/Staff)
    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return ResponseEntity.ok(feedbacks);
    }

    // Lấy phản hồi theo dịch vụ
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Feedback>> getFeedbackByService(@PathVariable Long serviceId) {
        List<Feedback> feedbacks = feedbackRepository.findByServiceId(serviceId);
        return ResponseEntity.ok(feedbacks);
    }
}