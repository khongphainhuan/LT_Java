package com.pascs.service;

import com.pascs.payload.request.ApplicationRequest;
import com.pascs.payload.response.ApplicationResponse;
import com.pascs.model.Application;
import com.pascs.model.Application.ApplicationStatus;
import com.pascs.model.User;
import com.pascs.exception.ResourceNotFoundException;
import com.pascs.repository.ApplicationRepository;
import com.pascs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Application Management
 * @author Dũng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ApplicationResponse submitApplication(ApplicationRequest request) {
        log.info("Submitting application for user: {}", request.getUserId());
        
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        Application application = new Application();
        application.setUser(user);
        application.setServiceId(request.getServiceId());
        application.setOfficeId(request.getOfficeId());
        application.setNotes(request.getNotes());
        application.submitApplication();
        
        application = applicationRepository.save(application);
        
        log.info("Application submitted: {}", application.getApplicationCode());
        return mapToResponse(application);
    }
    
    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        
        application.updateStatus(newStatus);
        application = applicationRepository.save(application);
        
        log.info("Application {} status updated to {}", id, newStatus);
        return mapToResponse(application);
    }
    
    @Transactional
    public ApplicationResponse assignStaff(Long id, Long staffId) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        
        application.setAssignedStaffId(staffId);
        application.updateStatus(ApplicationStatus.PROCESSING);
        application = applicationRepository.save(application);
        
        log.info("Application {} assigned to staff {}", id, staffId);
        return mapToResponse(application);
    }
    
    public ApplicationResponse getById(Long id) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        return mapToResponse(application);
    }
    
    public ApplicationResponse getByCode(String code) {
        Application application = applicationRepository.findByApplicationCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "code", code));
        return mapToResponse(application);
    }
    
    public List<ApplicationResponse> getUserApplications(Long userId) {
        return applicationRepository.findByUserIdOrderBySubmissionDateDesc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ApplicationResponse> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatusOrderBySubmissionDateDesc(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ApplicationResponse> getPendingApplications() {
        return applicationRepository.findPendingApplications()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ApplicationResponse> getStaffApplications(Long staffId) {
        return applicationRepository.findByAssignedStaffIdOrderBySubmissionDateDesc(staffId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ApplicationResponse> searchApplications(String keyword) {
        return applicationRepository.searchApplications(keyword)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private ApplicationResponse mapToResponse(Application app) {
        return ApplicationResponse.builder()
            .id(app.getId())
            .applicationCode(app.getApplicationCode())
            .userName(app.getUser() != null ? app.getUser().getFullName() : "Guest")
            .userId(app.getUser() != null ? app.getUser().getId() : null)
            .serviceId(app.getServiceId())
            .officeId(app.getOfficeId())
            .status(app.getStatus())
            .submissionDate(app.getSubmissionDate())
            .processingDate(app.getProcessingDate())
            .completionDate(app.getCompletionDate())
            .assignedStaffId(app.getAssignedStaffId())
            .notes(app.getNotes())
            .build();
    }
}
