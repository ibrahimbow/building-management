package com.why.buildingmanagement.building.infrastructure.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateBuildingRequest(
        @NotBlank(message = "building name required")
        String buildingName,

        @NotBlank(message = "address required")
        String address,

        @Min(value = 4, message = "total apartments must be at least 4")
        int totalApartments,

        @NotBlank(message = "emergency phone required")
        String emergencyPhone) {
}