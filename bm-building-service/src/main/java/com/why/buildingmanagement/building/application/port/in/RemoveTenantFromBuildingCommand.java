package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveTenantFromBuildingCommand(

        @NotNull(message = "building id required")
        UUID buildingId,

        @NotNull(message = "tenant user id required")
        Long tenantUserId,

        @NotNull(message = "manager id required")
        Long managerId) {
}