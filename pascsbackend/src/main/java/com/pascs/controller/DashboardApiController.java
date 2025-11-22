package com.pascs.controller;

import com.pascs.service.AnalyticsService;
import com.pascs.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    private AnalyticsService analyticsService;

    // Dashboard stats cho Admin
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboardStats() {
        Map<String, Object> stats = analyticsService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // Dashboard stats cho Staff
    @GetMapping("/staff/stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getStaffDashboardStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Stats cho staff member cụ thể
        Map<String, Object> stats = analyticsService.getStaffPerformanceStats(
                LocalDate.now().minusDays(7), 
                LocalDate.now());
        return ResponseEntity.ok(stats);
    }

    // Dashboard stats cho Citizen
    @GetMapping("/citizen/stats")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('ADMIN')")
    public ResponseEntity<?> getCitizenDashboardStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Stats cho citizen - có thể customize sau
        Map<String, Object> stats = analyticsService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // Thống kê hàng chờ theo ngày
    @GetMapping("/queue/daily-stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDailyQueueStats(@RequestParam(required = false) String date) {
        LocalDate reportDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> stats = analyticsService.getDailyQueueStats(reportDate);
        return ResponseEntity.ok(stats);
    }

    // Phân bổ dịch vụ
    @GetMapping("/service-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getServiceDistribution() {
        Map<String, Object> distribution = analyticsService.getServiceDistribution();
        return ResponseEntity.ok(distribution);
    }

    // Hiệu suất staff
    @GetMapping("/staff-performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStaffPerformance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        Map<String, Object> performance = analyticsService.getStaffPerformanceStats(start, end);
        return ResponseEntity.ok(performance);
    }

    // Tổng quan hệ thống
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSystemOverview() {
        Map<String, Object> overview = analyticsService.getAdminDashboardStats();
        Map<String, Object> serviceDistribution = analyticsService.getServiceDistribution();
        
        overview.put("serviceDistribution", serviceDistribution);
        return ResponseEntity.ok(overview);
    }
}

