package com.why.buildingmanagement.notification.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkNotificationAsReadCommand(

                @NotNull(message = "notificationId required")
                UUID notificationId,

                @NotNull(message = "userId required")
                Long userId) {
}