package com.pascs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Queue Ticket Entity - Quản lý phiếu hàng chờ
 * @author Dũng
 */
@Entity
@Table(name = "queue_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_number", nullable = false, length = 50)
    private String ticketNumber;
    
    @Column(name = "service_id", nullable = false)
    private Long serviceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  
    
    @Column(name = "office_id", nullable = false)
    private Long officeId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.WAITING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority = TicketPriority.NORMAL;
    
    @Column(name = "estimated_time")
    private LocalTime estimatedTime;
    
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "called_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calledAt;
    
    public void updateStatus(TicketStatus newStatus) {
        this.status = newStatus;
        if (newStatus == TicketStatus.CALLED || newStatus == TicketStatus.PROCESSING) {
            this.calledAt = LocalDateTime.now();
        }
    }
    
    public enum TicketStatus {
        WAITING, CALLED, PROCESSING, COMPLETED, CANCELLED
    }
    
    public enum TicketPriority {
        NORMAL, PRIORITY
    }
}