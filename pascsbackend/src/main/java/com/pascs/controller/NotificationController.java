package com.pascs.controller;

import com.pascs.model.Notification;
import com.pascs.payload.request.CreateNotificationRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.repository.NotificationRepository;
import com.pascs.repository.UserRepository;
import com.pascs.service.NotificationService;
import com.pascs.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Lấy tất cả thông báo của user hiện tại
    @GetMapping("")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getMyNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userDetails.getId());
        return ResponseEntity.ok(notifications);
    }

    // Lấy thông báo chưa đọc
    @GetMapping("/unread")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Notification> notifications = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        userDetails.getId(), 
                        Notification.NotificationStatus.UNREAD);
        return ResponseEntity.ok(notifications);
    }

    // Đếm số thông báo chưa đọc
    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getUnreadNotificationCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        long count = notificationRepository.countByUserIdAndStatus(
                userDetails.getId(), 
                Notification.NotificationStatus.UNREAD);
        return ResponseEntity.ok(new MessageResponse(String.valueOf(count)));
    }

    // Đánh dấu thông báo là đã đọc
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Notification", "id", id));

        // Kiểm tra quyền sở hữu
        if (!notification.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You don't have permission to access this notification"));
        }

        notification.setStatus(Notification.NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
    }

    // Đánh dấu tất cả thông báo là đã đọc
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        userDetails.getId(), 
                        Notification.NotificationStatus.UNREAD);

        for (Notification notification : unreadNotifications) {
            notification.setStatus(Notification.NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);

        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }

    // Xóa thông báo (chuyển sang archived)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Notification", "id", id));

        // Kiểm tra quyền sở hữu
        if (!notification.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You don't have permission to delete this notification"));
        }

        notification.setStatus(Notification.NotificationStatus.ARCHIVED);
        notificationRepository.save(notification);

        return ResponseEntity.ok(new MessageResponse("Notification archived"));
    }

    // Admin: Tạo thông báo cho user
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = new Notification();
            notification.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("User", "id", request.getUserId())));
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType() != null ? request.getType() : Notification.NotificationType.INFO);
            notification.setActionUrl(request.getActionUrl());
            notification.setRelatedEntityType(request.getRelatedEntityType());
            notification.setRelatedEntityId(request.getRelatedEntityId());

            Notification savedNotification = notificationRepository.save(notification);
            
            // Gửi thông báo qua WebSocket
            notificationService.sendPersonalNotification(
                    request.getUserId(), 
                    request.getTitle() + ": " + request.getMessage());

            return ResponseEntity.ok(savedNotification);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}

