package com.pascs.service;

import com.pascs.model.Application;
import com.pascs.model.Queue;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyQueueUpdate(Queue queue) {
        try {
            messagingTemplate.convertAndSend("/topic/queue-updates", queue);
            
            if (queue.getUser() != null) {
                String userDestination = "/queue/user-" + queue.getUser().getId();
                messagingTemplate.convertAndSend(userDestination, queue);
            }
        } catch (Exception e) {
            System.out.println("WebSocket error for queue update: " + e.getMessage());
        }
    }

    public void notifyApplicationStatus(Application application) {
        try {
            messagingTemplate.convertAndSend("/topic/application-updates", application);
            
            if (application.getUser() != null) {
                String userDestination = "/queue/user-" + application.getUser().getId();
                messagingTemplate.convertAndSend(userDestination, application);
            }
        } catch (Exception e) {
            System.out.println("WebSocket error for application update: " + e.getMessage());
        }
    }

    public void notifyStaffNewApplication(Application application) {
        try {
            messagingTemplate.convertAndSend("/topic/staff-applications", application);
        } catch (Exception e) {
            System.out.println("WebSocket error for staff application: " + e.getMessage());
        }
    }

    public void notifyStaffNewApplication(Queue queue) {
        try {
            messagingTemplate.convertAndSend("/topic/staff-queue", queue);
        } catch (Exception e) {
            System.out.println("WebSocket error for staff queue: " + e.getMessage());
        }
    }

    public void sendPersonalNotification(Long userId, String message) {
        try {
            String userDestination = "/queue/notifications-" + userId;
            messagingTemplate.convertAndSend(userDestination, message);
        } catch (Exception e) {
            System.out.println("WebSocket error for personal notification: " + e.getMessage());
        }
    }

    public void sendPersonalNotification(Long userId, String title, String message) {
        try {
            String userDestination = "/queue/notifications-" + userId;
            Map<String, String> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("message", message);
            messagingTemplate.convertAndSend(userDestination, notification);
        } catch (Exception e) {
            System.out.println("WebSocket error for personal notification: " + e.getMessage());
        }
    }
}