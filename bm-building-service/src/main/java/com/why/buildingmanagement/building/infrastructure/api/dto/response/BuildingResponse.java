package com.why.buildingmanagement.building.infrastructure.api.dto.response;

public record BuildingResponse(
        String id,
        String buildingName,
        String code,
        String address,
        String managerName,
        String managerEmail,
        int totalApartments,
        String emergencyPhone
) {}