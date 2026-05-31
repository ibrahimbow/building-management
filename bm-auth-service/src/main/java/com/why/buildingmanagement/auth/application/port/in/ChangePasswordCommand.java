package com.why.buildingmanagement.auth.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(

                @NotNull(message = "User id is required")
                Long userId,

                @NotBlank(message = "Current password is required")
                String currentPassword,

                @NotBlank(message = "New password is required")
                @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
                String newPassword) {
}