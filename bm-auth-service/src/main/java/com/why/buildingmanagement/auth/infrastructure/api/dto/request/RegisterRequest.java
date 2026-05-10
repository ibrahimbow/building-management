package com.why.buildingmanagement.auth.infrastructure.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

        @NotBlank(message = "username required")
        String username,

        @Email(message = "invalid email format")
        @NotBlank(message = "email required")
        String email,

        @NotBlank(message = "password required")
        String password,

        @NotBlank(message = "displayName required")
        String displayName,

        @NotBlank(message = "phone number required")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$",
                message = "invalid phone number format")
        String phoneNumber,

        @NotBlank(message = "role required")
        String role) {
}