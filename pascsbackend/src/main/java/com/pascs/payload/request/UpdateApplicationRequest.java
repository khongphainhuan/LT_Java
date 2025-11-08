package com.pascs.payload.request;

import com.pascs.model.Application;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationRequest {
    @NotNull
    private Application.ApplicationStatus status;
    
    private String note;
}