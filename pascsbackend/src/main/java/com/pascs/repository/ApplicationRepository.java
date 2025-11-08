package com.pascs.repository;

import com.pascs.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
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
}