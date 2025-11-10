package com.pascs.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueueRequest {
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    private boolean priority = false;
    
    private String specialRequirements;
    
    private String contactPreference; // SMS, EMAIL, APP
    
    // Lombok will generate getters
    public Long getServiceId() { return this.serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public boolean isPriority() { return this.priority; }
    public void setPriority(boolean priority) { this.priority = priority; }
    public String getSpecialRequirements() { return this.specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }
    public String getContactPreference() { return this.contactPreference; }
    public void setContactPreference(String contactPreference) { this.contactPreference = contactPreference; }
}