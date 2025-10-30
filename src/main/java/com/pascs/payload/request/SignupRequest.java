package com.pascs.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @Size(max = 15)
    private String phoneNumber;

    @Size(max = 200)
    private String address;
    
    // Lombok will generate getters
    // Explicit getters (workaround for annotation processor issues)
    public String getUsername() { return this.username; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public String getFullName() { return this.fullName; }
    public String getPhoneNumber() { return this.phoneNumber; }
    public String getAddress() { return this.address; }
}