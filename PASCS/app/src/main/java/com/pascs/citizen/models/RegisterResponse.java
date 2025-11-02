package com.pascs.citizen.models;

public class RegisterResponse {
    private boolean success;
    private String message;
    private User user;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getUser() { return user; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setUser(User user) { this.user = user; }
}