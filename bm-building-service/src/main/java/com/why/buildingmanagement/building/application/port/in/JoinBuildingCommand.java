package com.why.buildingmanagement.building.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record JoinBuildingCommand(

                @NotBlank(message = "building code required")
                String code,

                @NotNull(message = "tenant user id required")
                Long tenantUserId,

                @NotBlank(message = "tenant username required")
                String tenantUsername,

                @Email(message = "tenant email must be valid")
                @NotBlank(message = "tenant email required")
                String tenantEmail,

                @NotBlank(message = "tenant phone number required")
                @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "invalid phone number format")
                String tenantPhoneNumber) {
}