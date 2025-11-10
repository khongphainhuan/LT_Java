package com.pascs.controller;

import com.pascs.payload.response.DashboardStatsResponse;
import com.pascs.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboardStats() {
        Map<String, Object> stats = analyticsService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/charts/daily-queue")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDailyQueueChart() {
        Map<String, Object> chartData = analyticsService.getDailyQueueStats(LocalDate.now());
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/charts/service-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getServiceDistribution() {
        Map<String, Object> distribution = analyticsService.getServiceDistribution();
        return ResponseEntity.ok(distribution);
    }
}