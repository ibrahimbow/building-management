package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBuildingCommand(
        @NotBlank(message = "building name required")
        String buildingName,

        @NotBlank(message = "address required")
        String address,

        @NotNull(message = "manager id required")
        Long managerId,

        @Min(value = 4, message = "total apartments must be at least 4")
        int totalApartments,

        @NotBlank(message = "emergency phone required")
        String emergencyPhone) {
}