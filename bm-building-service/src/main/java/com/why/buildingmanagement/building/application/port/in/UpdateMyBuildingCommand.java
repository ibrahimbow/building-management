package com.why.buildingmanagement.building.application.port.in;

import java.util.UUID;

public record UpdateMyBuildingCommand(
        UUID buildingId,
        Long managerId,
        String buildingName,
        String address,
        int totalApartments,
        String emergencyPhone) {
}