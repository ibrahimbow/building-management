package com.why.buildingmanagement.chat.infrastructure.persistence.client;

import java.util.UUID;

public record ManagerBuildingResponse(
        UUID buildingId,
        String buildingName) {
}