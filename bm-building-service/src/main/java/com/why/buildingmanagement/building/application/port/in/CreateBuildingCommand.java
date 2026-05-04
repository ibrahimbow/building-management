package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateBuildingCommand(
        @NotBlank(message = "building name required")
        String buildingName,

        @NotBlank(message = "address required")
        String address,

        @NotBlank(message = "manager name required")
        String managerName,

        @Email(message = "manager email must be valid")
        @NotBlank(message = "manager email required")
        String managerEmail,

        @Min(value = 4, message = "total apartments must be at least 4")
        int totalApartments,

        @NotBlank(message = "emergency phone required")
        String emergencyPhone
) {
}