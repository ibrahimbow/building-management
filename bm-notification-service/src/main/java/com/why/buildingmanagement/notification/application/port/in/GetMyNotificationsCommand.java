package com.why.buildingmanagement.notification.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetMyNotificationsCommand(

                @NotNull(message = "userId required")
                Long userId,

                @NotNull(message = "buildingId required")
                UUID buildingId) {
}