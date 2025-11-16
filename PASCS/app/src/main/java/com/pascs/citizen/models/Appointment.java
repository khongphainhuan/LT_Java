package com.pascs.citizen.models;

public class Appointment {
    private int id;
    private String serviceType;
    private String appointmentDate;
    private String appointmentTime;
    private String status; // "pending", "confirmed", "cancelled", "completed"
    private String notes;
    private String createdAt;

    public Appointment(int id, String serviceType, String appointmentDate,
                       String appointmentTime, String status) {
        this.id = id;
        this.serviceType = serviceType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getServiceType() { return serviceType; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}