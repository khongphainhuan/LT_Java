package com.pascs.controller;

import com.pascs.model.Appointment;
import com.pascs.payload.request.AppointmentRequest;
import com.pascs.payload.response.MessageResponse;
import com.pascs.repository.AppointmentRepository;
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

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @PostMapping("")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            Appointment appointment = new Appointment();
            appointment.setUser(userRepository.findById(userDetails.getId()).orElseThrow());
            appointment.setService(serviceRepository.findById(appointmentRequest.getServiceId()).orElseThrow());
            appointment.setAppointmentTime(appointmentRequest.getAppointmentTime());
            appointment.setNote(appointmentRequest.getNote());

            Appointment savedAppointment = appointmentRepository.save(appointment);
            return ResponseEntity.ok(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<Appointment>> getMyAppointments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Appointment> appointments = appointmentRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/staff-appointments")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getStaffAppointments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Appointment> appointments = appointmentRepository.findByAssignedStaffId(userDetails.getId());
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(new MessageResponse("Appointment confirmed"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Appointment not found"));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(new MessageResponse("Appointment cancelled"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Appointment not found"));
        }
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam Long serviceId,
            @RequestParam String date) {
        // Implementation for available time slots
        List<String> slots = List.of("08:00", "09:00", "10:00", "11:00", "14:00", "15:00");
        return ResponseEntity.ok(slots);
    }
}