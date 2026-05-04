package com.why.buildingmanagement.building.application.result;

public record BuildingInfoResult(String id,
                                 String buildingName,
                                 String code,
                                 String address,
                                 String managerName,
                                 String managerEmail,
                                 int totalApartments,
                                 String emergencyPhone) {
}
