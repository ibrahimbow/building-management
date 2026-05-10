package com.why.buildingmanagement.announcement.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteAnnouncementCommand(

        @NotNull(message = "announcement id required")
        UUID announcementId,

        @NotNull(message = "manager id required")
        Long managerId) {
}