package com.pascs.citizen.models;

import java.util.List;

public class Application {
    private int id;
    private String serviceType;
    private String status; // "submitted", "processing", "need_supplement", "completed"
    private String submitDate;
    private String completedDate;
    private List<String> documents;
    private String notes;

    public Application(int id, String serviceType, String status, String submitDate) {
        this.id = id;
        this.serviceType = serviceType;
        this.status = status;
        this.submitDate = submitDate;
    }

    // Getters
    public int getId() { return id; }
    public String getServiceType() { return serviceType; }
    public String getStatus() { return status; }
    public String getSubmitDate() { return submitDate; }
    public String getCompletedDate() { return completedDate; }
    public List<String> getDocuments() { return documents; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setStatus(String status) { this.status = status; }
    public void setSubmitDate(String submitDate) { this.submitDate = submitDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
    public void setNotes(String notes) { this.notes = notes; }
}
