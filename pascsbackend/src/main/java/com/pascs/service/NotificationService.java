package com.pascs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for Real-time Notifications via WebSocket
 * @author Dũng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void notifyTicketCalled(Long userId, String ticketNumber, Long officeId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "TICKET_CALLED");
        notification.put("ticketNumber", ticketNumber);
        notification.put("officeId", officeId);
        notification.put("message", "Your ticket " + ticketNumber + " has been called");
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/queue/notifications", 
            notification
        );
        
        log.info("Notification sent: Ticket {} called for user {}", ticketNumber, userId);
    }
    
    public void notifyApplicationStatus(Long userId, String applicationCode, String status) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "APPLICATION_STATUS");
        notification.put("applicationCode", applicationCode);
        notification.put("status", status);
        notification.put("message", "Application " + applicationCode + " status: " + status);
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/queue/notifications", 
            notification
        );
        
        log.info("Notification sent: Application {} status {} for user {}", 
                 applicationCode, status, userId);
    }
    
    public void notifyOffice(Long officeId, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "OFFICE_BROADCAST");
        notification.put("officeId", officeId);
        notification.put("message", message);
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSend(
            "/topic/office/" + officeId, 
            notification
        );
        
        log.info("Broadcast sent to office {}: {}", officeId, message);
    }
    
    public void notifyQueueUpdate(Long officeId, Long waitingCount) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "QUEUE_UPDATE");
        notification.put("officeId", officeId);
        notification.put("waitingCount", waitingCount);
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSend(
            "/topic/queue/" + officeId, 
            notification
        );
        
        log.info("Queue update sent to office {}: {} waiting", officeId, waitingCount);
    }
}
