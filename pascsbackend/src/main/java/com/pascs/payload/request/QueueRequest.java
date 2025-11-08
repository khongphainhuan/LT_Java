package com.pascs.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueueRequest {
    @NotNull
    private Long serviceId;
}