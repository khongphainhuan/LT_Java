package com.pascs.controller;

import com.pascs.model.Queue;
import com.pascs.model.User;
import com.pascs.payload.request.QueueRequest;
import com.pascs.repository.QueueRepository;
import com.pascs.repository.ServiceRepository;
import com.pascs.repository.UserRepository;
import com.pascs.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pascs.exception.ResourceNotFoundException;
import com.pascs.payload.response.MessageResponse;
import com.pascs.model.Service;
import java.time.LocalDateTime;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/queues")
public class QueueController {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy số thứ tự
    @PostMapping("/take-number")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> takeQueueNumber(@Valid @RequestBody QueueRequest queueRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        Service service = serviceRepository.findById(queueRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", queueRequest.getServiceId()));

        Queue queue = new Queue();
        queue.setUser(user);
        queue.setService(service);
        queue.setStatus(Queue.QueueStatus.WAITING);

        queueRepository.save(queue);
        return ResponseEntity.ok(new MessageResponse("Queue number taken: " + queue.getTicketNumber()));
    }

    // Lấy danh sách hàng chờ hiện tại
    @GetMapping("/current")
    public ResponseEntity<List<Queue>> getCurrentQueue() {
        List<Queue> queues = queueRepository.findByStatusOrderByCheckInTimeAsc(Queue.QueueStatus.WAITING);
        return ResponseEntity.ok(queues);
    }

    // Gọi số tiếp theo
    @PostMapping("/call-next")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> callNextNumber(@RequestParam Long counterId) {
        List<Queue> waitingQueues = queueRepository.findByStatusOrderByCheckInTimeAsc(Queue.QueueStatus.WAITING);
        
        if (waitingQueues.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("No waiting customers"));
        }

        Queue nextQueue = waitingQueues.get(0);
        nextQueue.setStatus(Queue.QueueStatus.CALLED);
        nextQueue.setCalledTime(LocalDateTime.now());
        // Gán counter nếu cần

        queueRepository.save(nextQueue);
        return ResponseEntity.ok(nextQueue);
    }

    // Công dân xem số thứ tự của mình
    @GetMapping("/my-queue")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<Queue>> getMyQueue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Queue> queues = queueRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(queues);
    }
}