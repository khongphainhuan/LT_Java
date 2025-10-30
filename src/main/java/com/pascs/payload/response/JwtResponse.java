// File: src/main/java/com/pascs/payload/response/JwtResponse.java
package com.pascs.payload.response;

import com.pascs.model.User;
import lombok.Data;

@Data
public class JwtResponse {
    
    // ✅ Field 1: JWT Token string
    private String token;
    
    // ✅ Field 2: Token type (luôn là "Bearer")
    private String type = "Bearer";
    
    // ✅ Field 3: User ID
    private Long id;
    
    // ✅ Field 4: Username
    private String username;
    
    // ✅ Field 5: Email
    private String email;
    
    // ✅ Field 6: User Role (ADMIN, STAFF, CITIZEN)
    private User.UserRole role;

    // ✅ Constructor đầy đủ
    public JwtResponse(String token, Long id, String username, String email, User.UserRole role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // ✅ Getter methods
    public String getToken() { 
        return this.token; 
    }
    
    public String getType() { 
        return this.type; 
    }
    
    public Long getId() { 
        return this.id; 
    }
    
    public String getUsername() { 
        return this.username; 
    }
    
    public String getEmail() { 
        return this.email; 
    }
    
    public User.UserRole getRole() { 
        return this.role; 
    }
}