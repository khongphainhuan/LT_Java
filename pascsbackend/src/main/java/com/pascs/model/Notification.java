package com.pascs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.INFO;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String actionUrl; // URL for navigation
    private String relatedEntityType; // QUEUE, APPLICATION, etc.
    private Long relatedEntityId;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }

    public enum NotificationStatus {
        UNREAD, READ, ARCHIVED
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}