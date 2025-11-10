package com.pascs.payload.request;

import lombok.Data;

@Data
public class DocumentRequest {
    private String documentType;
    private String description;
    private String deadline;
}