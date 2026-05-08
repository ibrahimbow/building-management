package com.why.buildingmanagement.building.infrastructure.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AuthenticatedUser(

        @NotBlank(message = "user id required")
        String userId,

        @NotBlank(message = "email required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "username required")
        String username,

        @NotEmpty(message = "roles required")
        List<String> roles) {
}