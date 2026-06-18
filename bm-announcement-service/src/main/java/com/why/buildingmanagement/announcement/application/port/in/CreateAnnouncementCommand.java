package com.why.buildingmanagement.announcement.application.port.in;

import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAnnouncementCommand(

        @NotNull(message = "manager id required")
        Long managerId,

        @NotBlank(message = "created by required")
        @Size(max = 255, message = "created by must not exceed 255 characters")
        String createdBy,

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