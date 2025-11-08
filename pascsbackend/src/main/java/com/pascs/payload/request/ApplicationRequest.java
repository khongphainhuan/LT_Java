package com.pascs.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotNull
    private Long serviceId;
    
    private String description;
    private String documents;
}