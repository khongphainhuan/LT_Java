package com.pascs.repository;

import com.pascs.model.Application;  
import com.pascs.model.Application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Application operations
 * @author Dũng
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    Optional<Application> findByApplicationCode(String applicationCode);
    
    List<Application> findByUserIdOrderBySubmissionDateDesc(Long userId);
    
    List<Application> findByStatusOrderBySubmissionDateDesc(ApplicationStatus status);
    
    List<Application> findByAssignedStaffIdOrderBySubmissionDateDesc(Long staffId);
    
    @Query("SELECT a FROM Application a WHERE a.status = 'SUBMITTED' " +
           "AND a.assignedStaffId IS NULL ORDER BY a.submissionDate ASC")
    List<Application> findPendingApplications();
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = :status")
    Long countByStatus(@Param("status") ApplicationStatus status);
    
    @Query("SELECT a FROM Application a WHERE " +
           "LOWER(a.applicationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Application> searchApplications(@Param("keyword") String keyword);
}
