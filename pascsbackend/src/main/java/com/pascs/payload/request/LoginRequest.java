// File: src/main/java/com/pascs/payload/request/LoginRequest.java
package com.pascs.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    // ✅ Field 1: Username (bắt buộc)
    @NotBlank(message = "Username không được để trống")
    private String username;

    // ✅ Field 2: Password (bắt buộc)  
    @NotBlank(message = "Password không được để trống")
    private String password;

    // ✅ Getter methods (Lombok sẽ generate tự động)
    public String getUsername() { 
        return this.username; 
    }
    
    public String getPassword() { 
        return this.password; 
    }
    
    // ✅ Setter methods (Lombok sẽ generate tự động)
    public void setUsername(String username) {
        this.username = username;   
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}