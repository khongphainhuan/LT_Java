package com.pascs.service;

import com.pascs.payload.request.QueueTicketRequest;
import com.pascs.payload.response.QueueTicketResponse;
import com.pascs.model.QueueTicket;
import com.pascs.model.QueueTicket.TicketStatus;
import com.pascs.model.QueueTicket.TicketPriority;
import com.pascs.model.User;
import com.pascs.exception.ResourceNotFoundException;
import com.pascs.repository.QueueTicketRepository;
import com.pascs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Queue Management
 * @author Dũng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {
    
    private final QueueTicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public QueueTicketResponse generateTicket(QueueTicketRequest request) {
        log.info("Generating ticket for user: {}, office: {}", 
                 request.getUserId(), request.getOfficeId());
        
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        QueueTicket ticket = new QueueTicket();
        ticket.setUser(user);
        ticket.setServiceId(request.getServiceId());
        ticket.setOfficeId(request.getOfficeId());
        ticket.setTicketNumber(generateTicketNumber(request.getOfficeId()));
        ticket.setPriority(Boolean.TRUE.equals(request.getIsPriority()) ? 
            TicketPriority.PRIORITY : TicketPriority.NORMAL);
        ticket.setStatus(TicketStatus.WAITING);
        ticket.setEstimatedTime(LocalTime.of(0, 15));
        
        ticket = ticketRepository.save(ticket);
        
        log.info("Ticket generated: {}", ticket.getTicketNumber());
        return mapToResponse(ticket);
    }
    
    @Transactional
    public QueueTicketResponse callNextTicket(Long officeId) {
        log.info("Calling next ticket for office: {}", officeId);
        
        List<QueueTicket> waiting = ticketRepository.findWaitingTicketsByOffice(officeId);
        
        if (waiting.isEmpty()) {
            throw new ResourceNotFoundException("No tickets waiting in office " + officeId);
        }
        
        QueueTicket ticket = waiting.get(0);
        ticket.updateStatus(TicketStatus.CALLED);
        ticketRepository.save(ticket);
        
        log.info("Ticket called: {}", ticket.getTicketNumber());
        return mapToResponse(ticket);
    }
    
    @Transactional
    public QueueTicketResponse updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        QueueTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("QueueTicket", "id", ticketId));
        
        ticket.updateStatus(newStatus);
        ticket = ticketRepository.save(ticket);
        
        log.info("Ticket {} status updated to {}", ticketId, newStatus);
        return mapToResponse(ticket);
    }
    
    public QueueTicketResponse getTicketByNumber(String ticketNumber) {
        QueueTicket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new ResourceNotFoundException("QueueTicket", "ticketNumber", ticketNumber));
        
        return mapToResponse(ticket);
    }
    
    public List<QueueTicketResponse> getUserTicketsToday(Long userId) {
        return ticketRepository.findUserTicketsToday(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<QueueTicketResponse> getWaitingTickets(Long officeId) {
        return ticketRepository.findWaitingTicketsByOffice(officeId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public Long getWaitingCount(Long officeId) {
        return ticketRepository.countByOfficeIdAndStatus(officeId, TicketStatus.WAITING);
    }
    
    private String generateTicketNumber(Long officeId) {
        Long count = ticketRepository.countTodayTicketsByOffice(officeId);
        String prefix = count < 50 ? "A" : "B";
        return prefix + String.format("%03d", (count % 50) + 1);
    }
    
    private QueueTicketResponse mapToResponse(QueueTicket ticket) {
        Long waitingCount = ticketRepository.countByOfficeIdAndStatus(
            ticket.getOfficeId(), TicketStatus.WAITING);
        
        return QueueTicketResponse.builder()
            .id(ticket.getId())
            .ticketNumber(ticket.getTicketNumber())
            .userName(ticket.getUser() != null ? ticket.getUser().getFullName() : "Guest")
            .userId(ticket.getUser() != null ? ticket.getUser().getId() : null)
            .serviceId(ticket.getServiceId())
            .officeId(ticket.getOfficeId())
            .status(ticket.getStatus())
            .priority(ticket.getPriority())
            .estimatedTime(ticket.getEstimatedTime())
            .createdAt(ticket.getCreatedAt())
            .calledAt(ticket.getCalledAt())
            .waitingCount(waitingCount)
            .build();
    }
}
