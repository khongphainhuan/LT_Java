package com.pascs.citizen.models;

public class TakeQueueRequest {
    private int serviceId;
    private String notes;

    public TakeQueueRequest(int serviceId) {
        this.serviceId = serviceId;
    }

    public TakeQueueRequest(int serviceId, String notes) {
        this.serviceId = serviceId;
        this.notes = notes;
    }

    // Getters
    public int getServiceId() { return serviceId; }
    public String getNotes() { return notes; }

    // Setters
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setNotes(String notes) { this.notes = notes; }
}