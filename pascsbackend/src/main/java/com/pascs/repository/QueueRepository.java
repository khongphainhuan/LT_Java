package com.pascs.repository;

import com.pascs.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByTicketNumber(String ticketNumber);
    
    // CHỈNH SỬA - Thêm order by priority
    List<Queue> findByStatusOrderByPriorityDescCheckInTimeAsc(Queue.QueueStatus status);
    
    List<Queue> findByUserIdOrderByCheckInTimeDesc(Long userId);
    List<Queue> findByServiceId(Long serviceId);
    List<Queue> findByCounterId(Long counterId);
    
    // CÁC METHOD MỚI - DŨNG THÊM
    long countByStatus(Queue.QueueStatus status);
    
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.status = 'COMPLETED' AND DATE(q.completedTime) = CURRENT_DATE")
    long countCompletedToday();
    
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, q.checkInTime, q.completedTime)) FROM Queue q WHERE q.status = 'COMPLETED' AND q.completedTime IS NOT NULL")
    Integer calculateAverageWaitTime();
    
    List<Queue> findByAssignedStaffId(Long staffId);
}