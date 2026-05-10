package com.why.buildingmanagement.building.infrastructure.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CurrentUser(

        @NotNull(message = "user id required")
        Long userId,

        @NotBlank(message = "username required")
        @Size(max = 100, message = "username must not exceed 100 characters")
        String username,

        @NotBlank(message = "email required")
        @Email(message = "invalid email format")
        @Size(max = 255, message = "email must not exceed 255 characters")
        String email,

        @NotBlank(message = "phone number required")
        @Pattern(
                regexp = "^\\+?[0-9]{8,15}$",
                message = "invalid phone number format"
        )
        String phoneNumber,

        @NotBlank(message = "role required")
        @Size(max = 50, message = "role must not exceed 50 characters")
        String role
) {
}