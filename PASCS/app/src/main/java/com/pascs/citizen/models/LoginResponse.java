package com.pascs.citizen.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setToken(String token) { this.token = token; }
    public void setUser(User user) { this.user = user; }
}