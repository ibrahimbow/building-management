package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteMyBuildingCommand(
        @NotNull(message = "building id required")
        UUID buildingId,

        @NotNull(message = "manager id required")
        Long managerId) {
}