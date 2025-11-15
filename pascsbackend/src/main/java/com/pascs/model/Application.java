package com.pascs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Application Entity - Quản lý hồ sơ hành chính
 * @author Dũng
 */
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_code", unique = true, nullable = false, length = 50)
    private String applicationCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  
    
    @Column(name = "service_id", nullable = false)
    private Long serviceId;
    
    @Column(name = "office_id", nullable = false)
    private Long officeId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;
    
    @Column(name = "submission_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDate = LocalDateTime.now();
    
    @Column(name = "processing_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processingDate;
    
    @Column(name = "completion_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completionDate;
    
    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public void submitApplication() {
        this.applicationCode = generateApplicationCode();
        this.submissionDate = LocalDateTime.now();
        this.status = ApplicationStatus.SUBMITTED;
    }
    
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
        if (newStatus == ApplicationStatus.PROCESSING && this.processingDate == null) {
            this.processingDate = LocalDateTime.now();
        }
        if (newStatus == ApplicationStatus.COMPLETED) {
            this.completionDate = LocalDateTime.now();
        }
    }
    
    private String generateApplicationCode() {
        return "HS" + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd")) + 
            String.format("%03d", (int)(Math.random() * 1000));
    }
    
    public enum ApplicationStatus {
        SUBMITTED, PROCESSING, COMPLETED, REJECTED, CANCELLED
    }
}