package com.pascs.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceRequest {
    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    private String requiredDocuments;
    private Integer processingTime;
    private Double fee;

    // Explicit getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRequiredDocuments() { return requiredDocuments; }
    public Integer getProcessingTime() { return processingTime; }
    public Double getFee() { return fee; }
}