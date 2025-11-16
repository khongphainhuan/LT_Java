package com.pascs.citizen.models;

public class QueueTicket {
    private int ticketNumber;
    private String serviceName;
    private String status; // "waiting", "called", "serving", "completed"
    private int peopleAhead;
    private String estimatedTime;
    private String createdAt;

    public QueueTicket(int ticketNumber, String serviceName, String status,
                       int peopleAhead, String estimatedTime, String createdAt) {
        this.ticketNumber = ticketNumber;
        this.serviceName = serviceName;
        this.status = status;
        this.peopleAhead = peopleAhead;
        this.estimatedTime = estimatedTime;
        this.createdAt = createdAt;
    }

    // Getters
    public int getTicketNumber() { return ticketNumber; }
    public String getServiceName() { return serviceName; }
    public String getStatus() { return status; }
    public int getPeopleAhead() { return peopleAhead; }
    public String getEstimatedTime() { return estimatedTime; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setTicketNumber(int ticketNumber) { this.ticketNumber = ticketNumber; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setStatus(String status) { this.status = status; }
    public void setPeopleAhead(int peopleAhead) { this.peopleAhead = peopleAhead; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}