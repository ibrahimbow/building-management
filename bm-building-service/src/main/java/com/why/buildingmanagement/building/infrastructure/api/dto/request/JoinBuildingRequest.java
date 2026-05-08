package com.why.buildingmanagement.building.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JoinBuildingRequest(

        @NotBlank(message = "building code required")
        String code) {
}