package com.pascs.citizen.models;

public class FeedbackRequest {
    private int applicationId;
    private int serviceId;
    private int rating; // 1-5 sao
    private String comment;

    public FeedbackRequest(int applicationId, int rating, String comment) {
        this.applicationId = applicationId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getApplicationId() { return applicationId; }
    public int getServiceId() { return serviceId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    // Setters
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
}