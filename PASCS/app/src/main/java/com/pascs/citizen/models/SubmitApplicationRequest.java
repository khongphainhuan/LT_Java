package com.pascs.citizen.models;

import java.util.List;

public class SubmitApplicationRequest {
    private int serviceId;
    private List<String> documents;
    private String notes;

    public SubmitApplicationRequest(int serviceId, List<String> documents, String notes) {
        this.serviceId = serviceId;
        this.documents = documents;
        this.notes = notes;
    }

    // Getters
    public int getServiceId() { return serviceId; }
    public List<String> getDocuments() { return documents; }
    public String getNotes() { return notes; }

    // Setters
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
    public void setNotes(String notes) { this.notes = notes; }
}