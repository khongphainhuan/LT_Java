package com.pascs.payload.response;

import com.pascs.model.QueueTicket.TicketStatus;
import com.pascs.model.QueueTicket.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueTicketResponse {
    
    private Long id;
    private String ticketNumber;
    private String userName;
    private Long userId;
    private Long serviceId;
    private Long officeId;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalTime estimatedTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calledAt;
    
    private Long waitingCount;
}

