package com.why.buildingmanagement.auth.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateBuildingUserProfileRequest(

        @NotBlank(message = "Display name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Display name must be between 2 and 150 characters")
        String displayName,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{8,15}$",
                message = "Invalid phone number format")
        String phoneNumber,

        @Size(
                max = 500,
                message = "Avatar URL must not exceed 500 characters")
        String avatarUrl,

        @Size(
                max = 10,
                message = "Preferred language must not exceed 10 characters")
        String preferredLanguage,

        Boolean notificationsEnabled) {
}