package com.pascs.controller;

import com.pascs.model.Queue;
import com.pascs.payload.request.QueueRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.payload.response.QueueStatsResponse;
import com.pascs.service.QueueService;
import com.pascs.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/queues")
public class QueueController {

    @Autowired
    private com.pascs.repository.QueueRepository queueRepository;

    @Autowired
    private com.pascs.repository.ServiceRepository serviceRepository;

    @Autowired
    private com.pascs.repository.UserRepository userRepository;

    @Autowired
    private QueueService queueService;

    @PostMapping("/take-number")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> takeQueueNumber(@Valid @RequestBody QueueRequest queueRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            Queue queue = queueService.takeQueueNumber(userDetails.getId(), queueRequest);
            return ResponseEntity.ok(new MessageResponse("Queue number taken: " + queue.getTicketNumber()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/take-number-simple")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> takeQueueNumberSimple(@RequestParam Long serviceId, 
                                                 @RequestParam(defaultValue = "false") boolean priority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            Queue queue = queueService.takeQueueNumber(userDetails.getId(), serviceId, priority);
            return ResponseEntity.ok(new MessageResponse("Queue number taken: " + queue.getTicketNumber()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/call-next")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> callNextNumber(@RequestParam Long counterId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            Queue nextQueue = queueService.callNextNumber(counterId, userDetails.getId());
            return ResponseEntity.ok(nextQueue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<List<Queue>> getCurrentQueue() {
        List<Queue> queues = queueService.getCurrentQueue();
        return ResponseEntity.ok(queues);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<QueueStatsResponse> getQueueStatistics() {
        QueueService.QueueStats stats = queueService.getQueueStatistics();
        QueueStatsResponse response = new QueueStatsResponse(
            stats.getTotalWaiting(),
            stats.getTotalProcessing(),
            stats.getTotalCompletedToday(),
            stats.getAverageWaitTime()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/public")
    public ResponseEntity<QueueStatsResponse> getPublicQueueStatistics() {
        // Public endpoint for citizens to view queue statistics
        QueueService.QueueStats stats = queueService.getQueueStatistics();
        QueueStatsResponse response = new QueueStatsResponse(
            stats.getTotalWaiting(),
            stats.getTotalProcessing(),
            stats.getTotalCompletedToday(),
            stats.getAverageWaitTime()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{queueId}/complete")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> completeQueue(@PathVariable Long queueId) {
        try {
            Queue completedQueue = queueService.completeQueue(queueId);
            return ResponseEntity.ok(completedQueue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/{queueId}/cancel")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelQueue(@PathVariable Long queueId) {
        try {
            Queue cancelledQueue = queueService.cancelQueue(queueId);
            return ResponseEntity.ok(cancelledQueue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/counter/{counterId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getQueueByCounter(@PathVariable Long counterId) {
        try {
            List<Queue> queues = queueService.getQueueByCounter(counterId);
            return ResponseEntity.ok(queues);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/my-queue")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<Queue>> getMyQueue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Queue> queues = queueService.getUserQueue(userDetails.getId());
        return ResponseEntity.ok(queues);
    }

    @GetMapping("/{queueId}")
    public ResponseEntity<?> getQueueById(@PathVariable Long queueId) {
        try {
            Queue queue = queueRepository.findById(queueId)
                    .orElseThrow(() -> new com.pascs.exception.ResourceNotFoundException("Queue", "id", queueId));
            return ResponseEntity.ok(queue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}