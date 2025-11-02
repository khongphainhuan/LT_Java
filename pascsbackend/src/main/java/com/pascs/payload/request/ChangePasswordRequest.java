package com.pascs.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;

    // Lombok will generate getters
    // Explicit getters (workaround for annotation processor issues)
    public String getOldPassword() { return this.oldPassword; }
    public String getNewPassword() { return this.newPassword; }
}