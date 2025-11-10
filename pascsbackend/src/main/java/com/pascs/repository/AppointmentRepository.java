package com.pascs.repository;

import com.pascs.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByAssignedStaffId(Long staffId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsBetween(@Param("start") LocalDateTime start, 
                                            @Param("end") LocalDateTime end);
    
    long countByStatus(Appointment.AppointmentStatus status);
}