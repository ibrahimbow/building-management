package com.why.buildingmanagement.building.infrastructure.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinBuildingRequest(

        @NotBlank(message = "building code required")
        String code,

        @NotNull(message = "tenant user id required")
        Long tenantUserId,

        @Email(message = "tenant email must be valid")
        @NotBlank(message = "tenant email required")
        String tenantEmail) {

}