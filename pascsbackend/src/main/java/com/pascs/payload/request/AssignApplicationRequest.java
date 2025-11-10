package com.pascs.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignApplicationRequest {
    
    @NotNull(message = "Staff ID is required")
    private Long staffId;
    
    private String assignmentNote;
    
    private Integer priorityLevel = 1; // 1: Normal, 2: High, 3: Urgent
    
    private Integer estimatedProcessingTime; // in minutes
}