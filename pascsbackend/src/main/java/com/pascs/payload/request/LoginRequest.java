package com.pascs.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Lombok will generate getters
    // Explicit getters (workaround for annotation processor issues)
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
}