package com.why.buildingmanagement.announcement.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetManagerAnnouncementByIdQuery(
        @NotNull(message = "UUID is required")
        UUID announcementId,

        @NotNull(message = "manager id required")
        Long managerId) {
}