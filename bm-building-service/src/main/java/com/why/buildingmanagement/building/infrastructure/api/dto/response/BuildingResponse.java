package com.why.buildingmanagement.building.infrastructure.api.dto.response;

public record BuildingResponse(
        String id,
        String buildingName,
        String code,
        String address,
        Long managerId,
        String managerName,
        int totalApartments,
        String emergencyPhone) {
}