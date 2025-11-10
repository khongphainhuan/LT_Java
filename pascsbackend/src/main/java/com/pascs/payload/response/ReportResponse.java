package com.pascs.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private String reportId;
    private String reportName;
    private String reportType;
    private LocalDateTime generatedAt;
    private String downloadUrl;
    private String status; // GENERATING, COMPLETED, FAILED
    private String message;

    public ReportResponse(String reportId, String reportName, String reportType, 
                         LocalDateTime generatedAt, String status) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.generatedAt = generatedAt;
        this.status = status;
    }

    public ReportResponse(String reportId, String reportName, String reportType, 
                         LocalDateTime generatedAt, String status, String message) {
        this(reportId, reportName, reportType, generatedAt, status);
        this.message = message;
    }
}