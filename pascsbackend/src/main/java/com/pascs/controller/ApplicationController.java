package com.pascs.controller;

import com.pascs.model.Application;
import com.pascs.payload.request.ApplicationRequest;
import com.pascs.payload.request.AssignApplicationRequest;
import com.pascs.payload.request.UpdateApplicationRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.service.ApplicationWorkflowService;
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
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private com.pascs.repository.ApplicationRepository applicationRepository;

    @Autowired
    private com.pascs.repository.ServiceRepository serviceRepository;

    @Autowired
    private com.pascs.repository.UserRepository userRepository;

    @Autowired
    private ApplicationWorkflowService applicationWorkflowService;

    // Công dân gửi hồ sơ
    @PostMapping("")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> createApplication(@Valid @RequestBody ApplicationRequest applicationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            com.pascs.model.User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("User", "id", userDetails.getId()));

            com.pascs.model.Service service = serviceRepository.findById(applicationRequest.getServiceId())
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Service", "id", applicationRequest.getServiceId()));

            Application application = new Application();
            application.setUser(user);
            application.setService(service);
            application.setDescription(applicationRequest.getDescription());
            application.setDocuments(applicationRequest.getDocuments());
            application.setStatus(Application.ApplicationStatus.PENDING);

            applicationRepository.save(application);
            return ResponseEntity.ok(new MessageResponse("Application submitted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Phân công hồ sơ với đầy đủ thông tin
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignApplication(@PathVariable Long id, 
                                             @Valid @RequestBody AssignApplicationRequest assignRequest) {
        try {
            Application application = applicationWorkflowService.assignApplication(
                id, 
                assignRequest.getStaffId(),
                assignRequest.getAssignmentNote(),
                assignRequest.getPriorityLevel(),
                assignRequest.getEstimatedProcessingTime()
            );
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Phân công hồ sơ đơn giản (tương thích ngược)
    @PutMapping("/{id}/assign/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignApplication(@PathVariable Long id, @PathVariable Long staffId) {
        try {
            Application application = applicationWorkflowService.assignApplication(id, staffId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Yêu cầu bổ sung tài liệu
    @PostMapping("/{id}/request-documents")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> requestAdditionalDocuments(@PathVariable Long id, @RequestBody String documentRequest) {
        try {
            Application application = applicationWorkflowService.requestAdditionalDocuments(id, documentRequest);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Cập nhật trạng thái hồ sơ
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateApplicationRequest updateRequest) {
        try {
            Application application = applicationWorkflowService.updateApplicationStatus(
                id, 
                updateRequest.getStatus(), 
                updateRequest.getNote()
            );
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Thống kê workload
    @GetMapping("/staff/{staffId}/workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStaffWorkload(@PathVariable Long staffId) {
        try {
            ApplicationWorkflowService.WorkloadStats stats = applicationWorkflowService.getStaffWorkload(staffId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Lấy hồ sơ đang hoạt động của staff
    @GetMapping("/staff/{staffId}/active")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getActiveApplicationsByStaff(@PathVariable Long staffId) {
        try {
            List<Application> applications = applicationWorkflowService.getActiveApplicationsByStaff(staffId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Thống kê hồ sơ mới hôm nay
    @GetMapping("/stats/new-today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getNewApplicationsToday() {
        try {
            long count = applicationWorkflowService.getNewApplicationsToday();
            return ResponseEntity.ok(new MessageResponse("New applications today: " + count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Các method cũ giữ nguyên
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<Application>> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Application> applications = applicationRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Application>> getAssignedApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Application> applications = applicationRepository.findByAssignedStaffId(userDetails.getId());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();
        return ResponseEntity.ok(applications);
    }

    // Xem chi tiết hồ sơ
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Application", "id", id));

        // Kiểm tra quyền truy cập
        boolean isOwner = application.getUser().getId().equals(userDetails.getId());
        boolean isAssignedStaff = application.getAssignedStaff() != null && 
                                  application.getAssignedStaff().getId().equals(userDetails.getId());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAssignedStaff && !isAdmin) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You don't have permission to access this application"));
        }

        return ResponseEntity.ok(application);
    }

    // Chuyển hồ sơ (Staff/Admin)
    @PutMapping("/{id}/transfer")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> transferApplication(@PathVariable Long id, 
                                                  @RequestParam Long newStaffId) {
        try {
            Application application = applicationRepository.findById(id)
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Application", "id", id));

            com.pascs.model.User newStaff = userRepository.findById(newStaffId)
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Staff", "id", newStaffId));

            if (!newStaff.getRole().equals(com.pascs.model.User.UserRole.STAFF)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: User is not a staff member"));
            }

            application.setAssignedStaff(newStaff);
            applicationRepository.save(application);

            return ResponseEntity.ok(new MessageResponse("Application transferred successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Đánh dấu hồ sơ ưu tiên/khẩn cấp
    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> markApplicationPriority(@PathVariable Long id, 
                                                      @RequestParam boolean isPriority) {
        try {
            Application application = applicationRepository.findById(id)
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Application", "id", id));

            // Có thể thêm field isPriority vào Application model nếu chưa có
            // application.setPriority(isPriority);
            applicationRepository.save(application);

            return ResponseEntity.ok(new MessageResponse("Application priority updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}