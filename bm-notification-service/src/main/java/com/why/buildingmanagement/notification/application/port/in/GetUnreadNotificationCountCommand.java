package com.why.buildingmanagement.notification.application.port.in;

import jakarta.validation.constraints.NotNull;

public record GetUnreadNotificationCountCommand(

                @NotNull(message = "userId required")
                Long userId) {
}