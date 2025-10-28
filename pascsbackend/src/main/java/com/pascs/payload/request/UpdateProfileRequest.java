package com.pascs.payload.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
    private Boolean priorityEligible;

    // Lombok will generate getters
    // Explicit getters (workaround for annotation processor issues)
    public String getFullName() { return this.fullName; }
    public String getPhoneNumber() { return this.phoneNumber; }
    public String getAddress() { return this.address; }
    public Boolean getPriorityEligible() { return this.priorityEligible; }
}