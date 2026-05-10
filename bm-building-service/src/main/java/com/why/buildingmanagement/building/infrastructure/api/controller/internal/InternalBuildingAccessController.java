package com.why.buildingmanagement.building.infrastructure.api.controller.internal;

import com.why.buildingmanagement.building.application.port.in.GetMyBuildingUseCase;
import com.why.buildingmanagement.building.application.port.in.GetMyBuildingsUseCase;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingAccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalBuildingAccessController {

    private final GetMyBuildingsUseCase getMyBuildingsUseCase;
    private final GetMyBuildingUseCase getMyBuildingUseCase;

    @GetMapping("/managers/{managerId}/building")
    public BuildingAccessResponse getManagerBuilding(
            @PathVariable("managerId") final Long managerId) {

        final BuildingInfoResult building = getMyBuildingsUseCase.getMyBuildings(managerId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Manager has no building"));

        return new BuildingAccessResponse(
                UUID.fromString(building.id()),
                building.buildingName());
    }

    @GetMapping("/tenants/{tenantUserId}/active-building")
    public BuildingAccessResponse getTenantActiveBuilding(
            @PathVariable("tenantUserId") final Long tenantUserId) {

        final BuildingInfoResult building = getMyBuildingUseCase.getMyBuilding(tenantUserId);

        return new BuildingAccessResponse(
                UUID.fromString(building.id()),
                building.buildingName());
    }
}