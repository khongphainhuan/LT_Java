package com.pascs.service;

import com.pascs.model.Queue;
import com.pascs.model.User;
import com.pascs.payload.request.QueueRequest;
import com.pascs.repository.QueueRepository;
import com.pascs.repository.ServiceRepository;
import com.pascs.repository.UserRepository;
import com.pascs.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public Queue takeQueueNumber(Long userId, QueueRequest queueRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Sử dụng full qualified name để tránh conflict
        com.pascs.model.Service service = serviceRepository.findById(queueRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", queueRequest.getServiceId()));

        Queue queue = new Queue();
        queue.setUser(user);
        queue.setService(service);
        queue.setPriority(queueRequest.isPriority());
        queue.setStatus(Queue.QueueStatus.WAITING);
        queue.setCheckInTime(LocalDateTime.now());
        
        if (queueRequest.getSpecialRequirements() != null) {
            queue.setNote("Yêu cầu đặc biệt: " + queueRequest.getSpecialRequirements());
        }
        
        String ticketNumber = generateTicketNumber(service.getCode(), queueRequest.isPriority());
        queue.setTicketNumber(ticketNumber);
        
        calculateWaitTime(queue);
        
        Queue savedQueue = queueRepository.save(queue);
        notificationService.notifyQueueUpdate(savedQueue);
        
        return savedQueue;
    }

    public Queue takeQueueNumber(Long userId, Long serviceId, boolean isPriority) {
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setServiceId(serviceId);
        queueRequest.setPriority(isPriority);
        return takeQueueNumber(userId, queueRequest);
    }

    public Queue callNextNumber(Long counterId, Long staffId) {
        List<Queue> waitingQueues = queueRepository.findByStatusOrderByPriorityDescCheckInTimeAsc(Queue.QueueStatus.WAITING);
        
        if (waitingQueues.isEmpty()) {
            throw new ResourceNotFoundException("No waiting customers in queue");
        }

        Queue nextQueue = waitingQueues.stream()
                .filter(queue -> queue.isPriority())
                .findFirst()
                .orElse(waitingQueues.get(0));

        nextQueue.setStatus(Queue.QueueStatus.CALLED);
        nextQueue.setCalledTime(LocalDateTime.now());
        nextQueue.setCounterId(counterId);
        nextQueue.setAssignedStaffId(staffId);

        Queue updatedQueue = queueRepository.save(nextQueue);
        notificationService.notifyQueueUpdate(updatedQueue);
        notificationService.notifyStaffNewApplication(updatedQueue);

        return updatedQueue;
    }

    private void calculateWaitTime(Queue queue) {
        long waitingCount = queueRepository.countByStatus(Queue.QueueStatus.WAITING);
        int averageProcessingTime = 5;
        
        if (queue.isPriority()) {
            queue.setEstimatedWaitTime((int) (waitingCount * 3));
        } else {
            queue.setEstimatedWaitTime((int) (waitingCount * averageProcessingTime));
        }
    }

    private String generateTicketNumber(String serviceCode, boolean isPriority) {
        String prefix = isPriority ? "P" : "N";
        long timestamp = System.currentTimeMillis();
        return prefix + serviceCode + "-" + timestamp;
    }

    public Queue completeQueue(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));

        queue.setStatus(Queue.QueueStatus.COMPLETED);
        queue.setCompletedTime(LocalDateTime.now());

        Queue completedQueue = queueRepository.save(queue);
        notificationService.notifyQueueUpdate(completedQueue);

        return completedQueue;
    }

    public QueueStats getQueueStatistics() {
        long totalWaiting = queueRepository.countByStatus(Queue.QueueStatus.WAITING);
        long totalProcessing = queueRepository.countByStatus(Queue.QueueStatus.PROCESSING);
        long totalCompletedToday = queueRepository.countCompletedToday();
        int avgWaitTime = queueRepository.calculateAverageWaitTime();
        
        return new QueueStats(totalWaiting, totalProcessing, totalCompletedToday, avgWaitTime);
    }

    public List<Queue> getCurrentQueue() {
        return queueRepository.findByStatusOrderByPriorityDescCheckInTimeAsc(Queue.QueueStatus.WAITING);
    }

    public List<Queue> getUserQueue(Long userId) {
        return queueRepository.findByUserIdOrderByCheckInTimeDesc(userId);
    }

    public List<Queue> getQueueByCounter(Long counterId) {
        return queueRepository.findByCounterId(counterId);
    }

    public Queue cancelQueue(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));

        queue.setStatus(Queue.QueueStatus.CANCELLED);

        Queue cancelledQueue = queueRepository.save(queue);
        notificationService.notifyQueueUpdate(cancelledQueue);

        return cancelledQueue;
    }

    public static class QueueStats {
        private long totalWaiting;
        private long totalProcessing;
        private long totalCompletedToday;
        private int averageWaitTime;

        public QueueStats(long totalWaiting, long totalProcessing, long totalCompletedToday, int averageWaitTime) {
            this.totalWaiting = totalWaiting;
            this.totalProcessing = totalProcessing;
            this.totalCompletedToday = totalCompletedToday;
            this.averageWaitTime = averageWaitTime;
        }

        public long getTotalWaiting() { return totalWaiting; }
        public void setTotalWaiting(long totalWaiting) { this.totalWaiting = totalWaiting; }
        public long getTotalProcessing() { return totalProcessing; }
        public void setTotalProcessing(long totalProcessing) { this.totalProcessing = totalProcessing; }
        public long getTotalCompletedToday() { return totalCompletedToday; }
        public void setTotalCompletedToday(long totalCompletedToday) { this.totalCompletedToday = totalCompletedToday; }
        public int getAverageWaitTime() { return averageWaitTime; }
        public void setAverageWaitTime(int averageWaitTime) { this.averageWaitTime = averageWaitTime; }
    }
}