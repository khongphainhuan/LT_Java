package com.pascs.controller;

import com.pascs.model.Counter;
import com.pascs.model.User;
import com.pascs.payload.response.MessageResponse;
import com.pascs.repository.CounterRepository;
import com.pascs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/counters")
public class CounterController {

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Counter>> getAllCounters() {
        List<Counter> counters = counterRepository.findAll();
        return ResponseEntity.ok(counters);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Counter>> getActiveCounters() {
        List<Counter> counters = counterRepository.findByActiveTrue();
        return ResponseEntity.ok(counters);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCounter(@RequestBody Counter counter) {
        try {
            // Validate required fields
            if (counter.getName() == null || counter.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Counter name is required"));
            }

            if (counter.getLocation() == null || counter.getLocation().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Counter location is required"));
            }

            Counter savedCounter = counterRepository.save(counter);
            return ResponseEntity.ok(savedCounter);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCounter(@PathVariable Long id, @RequestBody Counter counter) {
        Optional<Counter> existingCounter = counterRepository.findById(id);
        if (existingCounter.isPresent()) {
            Counter existing = existingCounter.get();
            
            // Update fields from the incoming counter
            if (counter.getName() != null) {
                existing.setName(counter.getName());
            }
            if (counter.getLocation() != null) {
                existing.setLocation(counter.getLocation());
            }
            if (counter.getCurrentStaff() != null) {
                existing.setCurrentStaff(counter.getCurrentStaff());
            }
            if (counter.getCurrentQueueNumber() != null) {
                existing.setCurrentQueueNumber(counter.getCurrentQueueNumber());
            }
            existing.setActive(counter.isActive());
            
            Counter updatedCounter = counterRepository.save(existing);
            return ResponseEntity.ok(updatedCounter);
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @PutMapping("/{id}/assign-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignStaffToCounter(@PathVariable Long id, @RequestParam Long staffId) {
        Optional<Counter> counterOpt = counterRepository.findById(id);
        Optional<User> staffOpt = userRepository.findById(staffId);
        
        if (counterOpt.isPresent() && staffOpt.isPresent()) {
            Counter counter = counterOpt.get();
            User staff = staffOpt.get();
            
            // Check if user is actually a staff member
            if (!staff.getRole().equals(User.UserRole.STAFF)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: User is not a staff member"));
            }
            
            counter.setCurrentStaff(staff);
            Counter updatedCounter = counterRepository.save(counter);
            
            return ResponseEntity.ok(updatedCounter);
        } else {
            if (!counterOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Counter not found"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Staff member not found"));
            }
        }
    }

    @PutMapping("/{id}/unassign-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unassignStaffFromCounter(@PathVariable Long id) {
        Optional<Counter> counterOpt = counterRepository.findById(id);
        
        if (counterOpt.isPresent()) {
            Counter counter = counterOpt.get();
            counter.setCurrentStaff(null);
            counter.setCurrentQueueNumber(null);
            
            Counter updatedCounter = counterRepository.save(counter);
            return ResponseEntity.ok(updatedCounter);
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @PutMapping("/{id}/current-queue")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCurrentQueueNumber(@PathVariable Long id, @RequestParam String queueNumber) {
        Optional<Counter> counterOpt = counterRepository.findById(id);
        
        if (counterOpt.isPresent()) {
            Counter counter = counterOpt.get();
            counter.setCurrentQueueNumber(queueNumber);
            
            Counter updatedCounter = counterRepository.save(counter);
            return ResponseEntity.ok(updatedCounter);
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleCounterActive(@PathVariable Long id) {
        Optional<Counter> counterOpt = counterRepository.findById(id);
        
        if (counterOpt.isPresent()) {
            Counter counter = counterOpt.get();
            counter.setActive(!counter.isActive());
            
            Counter updatedCounter = counterRepository.save(counter);
            String status = counter.isActive() ? "activated" : "deactivated";
            return ResponseEntity.ok(new MessageResponse("Counter " + status + " successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCounter(@PathVariable Long id) {
        Optional<Counter> counterOpt = counterRepository.findById(id);
        
        if (counterOpt.isPresent()) {
            Counter counter = counterOpt.get();
            
            // Check if counter is currently assigned to staff
            if (counter.getCurrentStaff() != null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Cannot delete counter with assigned staff. Unassign staff first."));
            }
            
            counterRepository.delete(counter);
            return ResponseEntity.ok(new MessageResponse("Counter deleted successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getCounterById(@PathVariable Long id) {
        Optional<Counter> counter = counterRepository.findById(id);
        if (counter.isPresent()) {
            return ResponseEntity.ok(counter.get());
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Counter not found"));
        }
    }

    @GetMapping("/location/{location}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Counter>> getCountersByLocation(@PathVariable String location) {
        List<Counter> counters = counterRepository.findByLocation(location);
        return ResponseEntity.ok(counters);
    }
}