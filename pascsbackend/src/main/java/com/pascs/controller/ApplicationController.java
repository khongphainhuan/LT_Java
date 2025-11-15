package com.pascs.controller;

import com.pascs.payload.request.ApplicationRequest;
import com.pascs.payload.request.UpdateStatusRequest;
import com.pascs.payload.response.ApiResponse;
import com.pascs.payload.response.ApplicationResponse;
import com.pascs.model.Application.ApplicationStatus;
import com.pascs.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Application Management
 * @author Dũng
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    /**
     * Gửi hồ sơ mới
     * POST /api/applications/submit
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @Valid @RequestBody ApplicationRequest request) {
        
        log.info("API: Submit application for user {}", request.getUserId());
        ApplicationResponse response = applicationService.submitApplication(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Application submitted successfully", response));
    }
    
    /**
     * Cập nhật trạng thái hồ sơ
     * PUT /api/applications/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        log.info("API: Update application {} status to {}", id, request.getStatus());
        
        ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
        ApplicationResponse response = applicationService.updateStatus(id, status);
        
        return ResponseEntity.ok(
            ApiResponse.success("Status updated successfully", response));
    }
    
    /**
     * Phân công nhân viên
     * PUT /api/applications/{id}/assign/{staffId}
     */
    @PutMapping("/{id}/assign/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> assignStaff(
            @PathVariable Long id,
            @PathVariable Long staffId) {
        
        log.info("API: Assign application {} to staff {}", id, staffId);
        ApplicationResponse response = applicationService.assignStaff(id, staffId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Staff assigned successfully", response));
    }
    
    /**
     * Lấy hồ sơ theo ID
     * GET /api/applications/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getById(
            @PathVariable Long id) {
        
        log.info("API: Get application by id {}", id);
        ApplicationResponse response = applicationService.getById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy hồ sơ theo mã
     * GET /api/applications/code/{code}
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getByCode(
            @PathVariable String code) {
        
        log.info("API: Get application by code {}", code);
        ApplicationResponse response = applicationService.getByCode(code);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy hồ sơ của user
     * GET /api/applications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getUserApplications(
            @PathVariable Long userId) {
        
        log.info("API: Get applications for user {}", userId);
        List<ApplicationResponse> response = applicationService.getUserApplications(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy hồ sơ theo trạng thái
     * GET /api/applications/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getByStatus(
            @PathVariable String status) {
        
        log.info("API: Get applications by status {}", status);
        ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
        List<ApplicationResponse> response = applicationService.getApplicationsByStatus(appStatus);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy hồ sơ chờ xử lý
     * GET /api/applications/pending
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getPendingApplications() {
        
        log.info("API: Get pending applications");
        List<ApplicationResponse> response = applicationService.getPendingApplications();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy hồ sơ được phân cho nhân viên
     * GET /api/applications/staff/{staffId}
     */
    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getStaffApplications(
            @PathVariable Long staffId) {
        
        log.info("API: Get applications for staff {}", staffId);
        List<ApplicationResponse> response = applicationService.getStaffApplications(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Tìm kiếm hồ sơ
     * GET /api/applications/search?keyword=xxx
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> searchApplications(
            @RequestParam String keyword) {
        
        log.info("API: Search applications with keyword: {}", keyword);
        List<ApplicationResponse> response = applicationService.searchApplications(keyword);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
