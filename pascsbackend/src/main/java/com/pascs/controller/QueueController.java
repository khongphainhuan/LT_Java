package com.pascs.controller;

import com.pascs.payload.request.QueueTicketRequest;
import com.pascs.payload.request.UpdateStatusRequest;
import com.pascs.payload.response.ApiResponse;
import com.pascs.payload.response.QueueTicketResponse;
import com.pascs.model.QueueTicket.TicketStatus;
import com.pascs.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Queue Management
 * @author Dũng
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueController {
    
    private final QueueService queueService;
    
    /**
     * Tạo phiếu hàng chờ mới
     * POST /api/queue/generate
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> generateTicket(
            @Valid @RequestBody QueueTicketRequest request) {
        
        log.info("API: Generate ticket for user {}", request.getUserId());
        QueueTicketResponse response = queueService.generateTicket(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Ticket generated successfully", response));
    }
    
    /**
     * Gọi số tiếp theo
     * POST /api/queue/call/{officeId}
     */
    @PostMapping("/call/{officeId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> callNextTicket(
            @PathVariable Long officeId) {
        
        log.info("API: Call next ticket for office {}", officeId);
        QueueTicketResponse response = queueService.callNextTicket(officeId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Ticket called successfully", response));
    }
    
    /**
     * Cập nhật trạng thái phiếu
     * PUT /api/queue/{ticketId}/status
     */
    @PutMapping("/{ticketId}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> updateStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        log.info("API: Update ticket {} status to {}", ticketId, request.getStatus());
        
        TicketStatus status = TicketStatus.valueOf(request.getStatus().toUpperCase());
        QueueTicketResponse response = queueService.updateTicketStatus(ticketId, status);
        
        return ResponseEntity.ok(
            ApiResponse.success("Status updated successfully", response));
    }
    
    /**
     * Lấy thông tin phiếu theo số
     * GET /api/queue/ticket/{ticketNumber}
     */
    @GetMapping("/ticket/{ticketNumber}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> getTicketByNumber(
            @PathVariable String ticketNumber) {
        
        log.info("API: Get ticket by number {}", ticketNumber);
        QueueTicketResponse response = queueService.getTicketByNumber(ticketNumber);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy danh sách phiếu của user hôm nay
     * GET /api/queue/user/{userId}/today
     */
    @GetMapping("/user/{userId}/today")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<QueueTicketResponse>>> getUserTicketsToday(
            @PathVariable Long userId) {
        
        log.info("API: Get today tickets for user {}", userId);
        List<QueueTicketResponse> response = queueService.getUserTicketsToday(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Lấy danh sách đang chờ
     * GET /api/queue/office/{officeId}/waiting
     */
    @GetMapping("/office/{officeId}/waiting")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<QueueTicketResponse>>> getWaitingTickets(
            @PathVariable Long officeId) {
        
        log.info("API: Get waiting tickets for office {}", officeId);
        List<QueueTicketResponse> response = queueService.getWaitingTickets(officeId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Đếm số người đang chờ
     * GET /api/queue/office/{officeId}/count
     */
    @GetMapping("/office/{officeId}/count")
    public ResponseEntity<ApiResponse<Long>> getWaitingCount(
            @PathVariable Long officeId) {
        
        Long count = queueService.getWaitingCount(officeId);
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
