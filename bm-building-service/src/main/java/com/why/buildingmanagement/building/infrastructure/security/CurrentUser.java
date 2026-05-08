package com.why.buildingmanagement.building.infrastructure.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CurrentUser(

        @NotNull(message = "user id required")
        Long userId,

        @NotBlank(message = "username required")
        String username,

        @NotBlank(message = "email required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "role required")
        String role) {
}