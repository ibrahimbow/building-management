package com.why.buildingmanagement.announcement.infrastructure.api.dto.request;

import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAnnouncementRequest(

        @NotBlank(message = "title required")
        @Size(max = 255, message = "title must not exceed 255 characters")
        String title,

        @NotBlank(message = "message required")
        @Size(max = 5000, message = "message must not exceed 5000 characters")
        String message,

        @NotNull(message = "category required")
        AnnouncementCategory category,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}