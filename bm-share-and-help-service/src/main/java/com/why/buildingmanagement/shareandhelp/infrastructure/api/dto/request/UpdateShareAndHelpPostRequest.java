package com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateShareAndHelpPostRequest(

        @NotBlank(message = "title required")
        @Size(max = 150, message = "title must not exceed 150 characters")
        String title,

        @NotBlank(message = "description required")
        @Size(max = 5000, message = "description must not exceed 5000 characters")
        String description,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}