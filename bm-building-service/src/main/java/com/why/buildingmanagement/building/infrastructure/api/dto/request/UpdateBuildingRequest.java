package com.why.buildingmanagement.building.infrastructure.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateBuildingRequest(

        @NotBlank(message = "Building name is required")
        String buildingName,

        @NotBlank(message = "Address is required")
        String address,

        @Min(value = 4, message = "Total apartments must be at least 4")
        int totalApartments,

        @NotBlank(message = "Emergency phone is required")
        String emergencyPhone) {
}