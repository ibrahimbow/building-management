package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateShareAndHelpPostCommand(

        @NotNull(message = "post id required")
        UUID postId,

        @NotNull(message = "user id required")
        Long userId,

        @NotBlank(message = "title required")
        @Size(max = 150, message = "title must not exceed 150 characters")
        String title,

        @NotBlank(message = "description required")
        @Size(max = 5000, message = "description must not exceed 5000 characters")
        String description,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}