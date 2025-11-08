package com.pascs.repository;

import com.pascs.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByTicketNumber(String ticketNumber);
    List<Queue> findByStatusOrderByCheckInTimeAsc(Queue.QueueStatus status);
    List<Queue> findByUserId(Long userId);
    List<Queue> findByServiceId(Long serviceId);
    List<Queue> findByCounterId(Long counterId);
}