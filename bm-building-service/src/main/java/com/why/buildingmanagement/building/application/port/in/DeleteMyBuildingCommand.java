package com.why.buildingmanagement.building.application.port.in;

import java.util.UUID;

public record DeleteMyBuildingCommand(
        UUID buildingId,
        Long managerId) {
}