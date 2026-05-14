package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateShareAndHelpPostCommand(

        @NotNull(message = "building id required")
        UUID buildingId,

        @NotNull(message = "created by user id required")
        Long createdByUserId,

        @NotBlank(message = "created by display name required")
        String createdByDisplayName,

        String createdByAvatarUrl,

        @NotBlank(message = "title required")
        String title,

        @NotBlank(message = "description required")
        String description,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}