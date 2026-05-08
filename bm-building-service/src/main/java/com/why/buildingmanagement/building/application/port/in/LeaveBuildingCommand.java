package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.NotNull;

public record LeaveBuildingCommand(
        @NotNull(message = "tenant user id required")
        Long tenantUserId) {
}
