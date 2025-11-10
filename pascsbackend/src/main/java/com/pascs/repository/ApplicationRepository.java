package com.pascs.repository;

import com.pascs.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByApplicationCode(String applicationCode);
    List<Application> findByUserId(Long userId);
    List<Application> findByAssignedStaffId(Long staffId);
    List<Application> findByStatus(Application.ApplicationStatus status);
    List<Application> findByServiceId(Long serviceId);
    
    // CÁC METHOD MỚI - DŨNG THÊM
    long countByAssignedStaffId(Long staffId);
    long countByAssignedStaffIdAndStatus(Long staffId, Application.ApplicationStatus status);
    
    @Query("SELECT a FROM Application a WHERE a.assignedStaff.id = :staffId AND a.status = 'PROCESSING'")
    List<Application> findActiveApplicationsByStaff(@Param("staffId") Long staffId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'PENDING' AND a.submittedAt >= CURRENT_DATE")
    long countNewApplicationsToday();
    
    // Thêm method mới
    long countByStatus(Application.ApplicationStatus status);
}