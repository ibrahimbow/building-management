package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateMyBuildingCommand(

        @NotNull(message = "building id required")
        UUID buildingId,

        @NotNull(message = "manager id required")
        Long managerId,

        @NotBlank(message = "building name required")
        String buildingName,

        @NotBlank(message = "address required")
        String address,

        @Min(value = 4, message = "total apartments must be at least 4")
        int totalApartments,

        @NotBlank(message = "emergency phone required")
        String emergencyPhone) {
}