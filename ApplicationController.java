package com.pascs.controller;

import com.pascs.model.Application;
import com.pascs.model.User;
import com.pascs.payload.request.ApplicationRequest;
import com.pascs.payload.request.UpdateApplicationRequest;
import com.pascs.repository.ApplicationRepository;
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

import com.pascs.exception.ResourceNotFoundException;
import com.pascs.payload.response.MessageResponse;
import com.pascs.model.Service;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // Công dân gửi hồ sơ
    @PostMapping("")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> createApplication(@Valid @RequestBody ApplicationRequest applicationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        Service service = serviceRepository.findById(applicationRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", applicationRequest.getServiceId()));

        Application application = new Application();
        application.setUser(user);
        application.setService(service);
        application.setDescription(applicationRequest.getDescription());
        application.setDocuments(applicationRequest.getDocuments());
        application.setStatus(Application.ApplicationStatus.PENDING);

        applicationRepository.save(application);
        return ResponseEntity.ok(new MessageResponse("Application submitted successfully!"));
    }

    // Công dân xem hồ sơ của mình
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<Application>> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Application> applications = applicationRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(applications);
    }

    // Cán bộ xem hồ sơ được phân công
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Application>> getAssignedApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Application> applications = applicationRepository.findByAssignedStaffId(userDetails.getId());
        return ResponseEntity.ok(applications);
    }

    // Cập nhật trạng thái hồ sơ
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateApplicationRequest updateRequest) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        application.setStatus(updateRequest.getStatus());
        application.setNote(updateRequest.getNote());

        if (updateRequest.getStatus() == Application.ApplicationStatus.PROCESSING) {
            application.setProcessedAt(LocalDateTime.now());
        } else if (updateRequest.getStatus() == Application.ApplicationStatus.COMPLETED) {
            application.setCompletedAt(LocalDateTime.now());
        }

        applicationRepository.save(application);
        return ResponseEntity.ok(new MessageResponse("Application status updated successfully!"));
    }

    // Admin/Cán bộ xem tất cả hồ sơ
    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();
        return ResponseEntity.ok(applications);
    }
}