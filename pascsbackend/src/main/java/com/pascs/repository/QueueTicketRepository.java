package com.pascs.repository;

import com.pascs.model.QueueTicket;  
import com.pascs.model.QueueTicket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Queue Ticket operations
 * @author Dũng
 */
@Repository
public interface QueueTicketRepository extends JpaRepository<QueueTicket, Long> {
    
    Optional<QueueTicket> findByTicketNumber(String ticketNumber);
    
    List<QueueTicket> findByUserId(Long userId);
    
    List<QueueTicket> findByOfficeIdAndStatus(Long officeId, TicketStatus status);
    
    @Query("SELECT t FROM QueueTicket t WHERE t.user.id = :userId " +
           "AND DATE(t.createdAt) = CURRENT_DATE ORDER BY t.createdAt DESC")
    List<QueueTicket> findUserTicketsToday(@Param("userId") Long userId);
    
    @Query("SELECT t FROM QueueTicket t WHERE t.officeId = :officeId " +
           "AND t.status = 'WAITING' " +
           "ORDER BY CASE WHEN t.priority = 'PRIORITY' THEN 0 ELSE 1 END, t.createdAt ASC")
    List<QueueTicket> findWaitingTicketsByOffice(@Param("officeId") Long officeId);
    
    Long countByOfficeIdAndStatus(Long officeId, TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM QueueTicket t WHERE t.officeId = :officeId " +
           "AND DATE(t.createdAt) = CURRENT_DATE")
    Long countTodayTicketsByOffice(@Param("officeId") Long officeId);
}

