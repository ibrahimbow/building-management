package com.why.buildingmanagement.notification.infrastructure.client;

import java.util.UUID;

public record ManagerBuildingResponse(
                UUID buildingId,
                String buildingName) {
}