package com.pascs.citizen.models;

public class Service {
    private int id;
    private String name;
    private String category;
    private String description;
    private int estimatedTime; // phút

    public Service(int id, String name, String category, String description, int estimatedTime) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.estimatedTime = estimatedTime;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getEstimatedTime() { return estimatedTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }
}