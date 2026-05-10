package com.why.buildingmanagement.building.infrastructure.api.dto.response;

import java.util.UUID;

public record BuildingAccessResponse(
        UUID buildingId,
        String buildingName) {
}