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

    private Integer estimatedWaitTime;

    @ManyToOne
    @JoinColumn(name = "counter_id")
    private Counter counter;

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
}

@Entity
@Table(name = "counters")
@Data
class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private boolean active = true;
}