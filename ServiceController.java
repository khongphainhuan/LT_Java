package com.pascs.controller;

import com.pascs.model.Service;
import com.pascs.payload.request.ServiceRequest;
import com.pascs.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pascs.exception.ResourceNotFoundException;
import com.pascs.payload.response.MessageResponse;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    // Lấy tất cả dịch vụ (public)
    @GetMapping("")
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceRepository.findAll();
        return ResponseEntity.ok(services);
    }

    // Lấy dịch vụ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "id", id));
        return ResponseEntity.ok(service);
    }

    // Tạo dịch vụ mới (chỉ ADMIN)
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceRequest serviceRequest) {
        if (serviceRepository.existsByCode(serviceRequest.getCode())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Service code is already taken!"));
        }

        Service service = new Service();
        service.setCode(serviceRequest.getCode());
        service.setName(serviceRequest.getName());
        service.setDescription(serviceRequest.getDescription());
        service.setRequiredDocuments(serviceRequest.getRequiredDocuments());
        service.setProcessingTime(serviceRequest.getProcessingTime());
        service.setFee(serviceRequest.getFee());
        service.setStatus(Service.ServiceStatus.ACTIVE);

        serviceRepository.save(service);
        return ResponseEntity.ok(new MessageResponse("Service created successfully!"));
    }

    // Cập nhật dịch vụ (chỉ ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateService(@PathVariable Long id, 
                                         @Valid @RequestBody ServiceRequest serviceRequest) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "id", id));

        service.setName(serviceRequest.getName());
        service.setDescription(serviceRequest.getDescription());
        service.setRequiredDocuments(serviceRequest.getRequiredDocuments());
        service.setProcessingTime(serviceRequest.getProcessingTime());
        service.setFee(serviceRequest.getFee());

        serviceRepository.save(service);
        return ResponseEntity.ok(new MessageResponse("Service updated successfully!"));
    }

    // Xóa dịch vụ (chỉ ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "id", id));
        
        serviceRepository.delete(service);
        return ResponseEntity.ok(new MessageResponse("Service deleted successfully!"));
    }
}