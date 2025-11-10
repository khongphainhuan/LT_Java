package com.pascs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String applicationCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private String documents;

    @ManyToOne
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    // CÁC FIELD MỚI - DŨNG THÊM
    private Integer processingStep = 1;
    private String currentStepName = "Tiếp nhận hồ sơ";
    private Boolean urgent = false;
    private String contactInfo;
    private String specialRequirements;

    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;
    private LocalDateTime completedAt;
    private String note;

    public enum ApplicationStatus {
        PENDING, PROCESSING, NEEDS_SUPPLEMENT, COMPLETED, CANCELLED
    }

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
        if (this.applicationCode == null) {
            this.applicationCode = "APP" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == ApplicationStatus.PROCESSING && this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
        if (this.status == ApplicationStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}