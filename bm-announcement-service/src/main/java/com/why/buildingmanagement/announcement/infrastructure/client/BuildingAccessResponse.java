package com.why.buildingmanagement.announcement.infrastructure.client;

import java.util.UUID;

public record BuildingAccessResponse(
        UUID buildingId,
        String buildingName) {
}