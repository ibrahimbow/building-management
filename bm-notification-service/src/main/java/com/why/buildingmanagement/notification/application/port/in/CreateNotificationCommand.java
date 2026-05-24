package com.why.buildingmanagement.notification.application.port.in;

import com.why.buildingmanagement.notification.domain.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateNotificationCommand(

                @NotNull(message = "userId required")
                Long userId,

                @NotNull(message = "buildingId required")
                UUID buildingId,

                @NotNull(message = "type required")
                NotificationType type,

                @NotBlank(message = "title required")
                @Size(max = 255, message = "title must not exceed 255 characters")
                String title,

                @NotBlank(message = "message required")
                @Size(max = 5000, message = "message must not exceed 5000 characters")
                String message) {
}