package com.pascs.payload.request;

import com.pascs.model.Notification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateNotificationRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1, max = 200)
    private String title;

    @Size(max = 1000)
    private String message;

    private Notification.NotificationType type;

    private String actionUrl;

    private String relatedEntityType;

    private Long relatedEntityId;

    // Explicit getters
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Notification.NotificationType getType() { return type; }
    public String getActionUrl() { return actionUrl; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public Long getRelatedEntityId() { return relatedEntityId; }
}

