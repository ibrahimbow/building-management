package com.why.buildingmanagement.announcement.application.port.in;

import jakarta.validation.constraints.NotNull;

public record GetTenantAnnouncementsQuery(

        @NotNull(message = "tenant user id required")
        Long tenantUserId) {
}