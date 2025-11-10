package com.pascs.controller;

import com.pascs.model.SystemConfig;
import com.pascs.payload.response.MessageResponse;
import com.pascs.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemConfig>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigRepository.findAll();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getConfig(@PathVariable String key) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(key);
        if (config.isPresent()) {
            return ResponseEntity.ok(config.get());
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Config not found"));
        }
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateConfig(@PathVariable String key, @RequestBody SystemConfig config) {
        Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(key);
        if (existingConfig.isPresent()) {
            SystemConfig updatedConfig = existingConfig.get();
            updatedConfig.setConfigValue(config.getConfigValue());
            updatedConfig.setDescription(config.getDescription());
            
            systemConfigRepository.save(updatedConfig);
            return ResponseEntity.ok(new MessageResponse("Config updated successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Config not found"));
        }
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkUpdateConfigs(@RequestBody List<SystemConfig> configs) {
        try {
            for (SystemConfig config : configs) {
                Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(config.getConfigKey());
                if (existingConfig.isPresent()) {
                    SystemConfig updatedConfig = existingConfig.get();
                    updatedConfig.setConfigValue(config.getConfigValue());
                    systemConfigRepository.save(updatedConfig);
                }
            }
            return ResponseEntity.ok(new MessageResponse("Configs updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/system-settings")
    public ResponseEntity<Map<String, String>> getSystemSettings() {
        // Return common settings that don't require admin role
        Map<String, String> settings = new HashMap<>();
        settings.put("systemName", "PASCS");
        settings.put("version", "1.0.0");
        settings.put("supportEmail", "support@pascs.com");
        settings.put("workingHours", "07:30-17:30");
        
        return ResponseEntity.ok(settings);
    }
}