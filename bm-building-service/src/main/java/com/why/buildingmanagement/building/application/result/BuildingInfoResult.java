package com.why.buildingmanagement.building.application.result;

public record BuildingInfoResult(
        String id,
        String buildingName,
        String code,
        String address,
        Long managerId,
        String managerName,
        int totalApartments,
        String emergencyPhone) {
}