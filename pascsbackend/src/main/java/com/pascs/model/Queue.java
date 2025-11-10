package com.pascs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "queues")
@Data
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Enumerated(EnumType.STRING)
    private QueueStatus status = QueueStatus.WAITING;

    private boolean priority = false;
    private Integer estimatedWaitTime;
    private Long counterId;
    private Long assignedStaffId;
    private String note;
    private String contactPreference;
    private LocalDateTime checkInTime;
    private LocalDateTime calledTime;
    private LocalDateTime completedTime;

    public enum QueueStatus {
        WAITING, CALLED, PROCESSING, COMPLETED, CANCELLED
    }

    @PrePersist
    public void prePersist() {
        this.checkInTime = LocalDateTime.now();
        if (this.ticketNumber == null) {
            this.ticketNumber = "T" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == QueueStatus.CALLED && this.calledTime == null) {
            this.calledTime = LocalDateTime.now();
        }
        if (this.status == QueueStatus.COMPLETED && this.completedTime == null) {
            this.completedTime = LocalDateTime.now();
        }
    }
}