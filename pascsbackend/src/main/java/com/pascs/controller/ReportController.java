package com.pascs.controller;

import com.pascs.payload.response.MessageResponse;
import com.pascs.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/queue-daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateDailyQueueReport(@RequestParam String date) {
        try {
            LocalDate reportDate = LocalDate.parse(date);
            Resource reportFile = reportService.generateDailyQueueReport(reportDate);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"queue-report-" + date + ".pdf\"")
                    .body(reportFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/service-performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateServicePerformanceReport(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Resource reportFile = reportService.generateServicePerformanceReport(start, end);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"service-performance-" + start + "-to-" + end + ".pdf\"")
                    .body(reportFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/staff-workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateStaffWorkloadReport(@RequestParam String month) {
        try {
            Resource reportFile = reportService.generateStaffWorkloadReport(month);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"staff-workload-" + month + ".pdf\"")
                    .body(reportFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReportStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        Map<String, Object> stats = reportService.getReportStatistics(start, end);
        return ResponseEntity.ok(stats);
    }
}