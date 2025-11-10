package com.pascs.payload.response;

import com.pascs.model.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private User.UserRole role;
    private boolean priorityEligible;
    private LocalDateTime createdAt;

    public UserProfileResponse(Long id, String username, String email, String fullName, 
                              String phoneNumber, String address, User.UserRole role, 
                              boolean priorityEligible, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.priorityEligible = priorityEligible;
        this.createdAt = createdAt;
    }
}