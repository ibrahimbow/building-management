package com.why.buildingmanagement.building.infrastructure.api.controller.internal;

import com.why.buildingmanagement.building.application.port.in.GetMyBuildingUseCase;
import com.why.buildingmanagement.building.application.port.in.GetMyBuildingsUseCase;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingAccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalBuildingAccessController {

    private final GetMyBuildingsUseCase getMyBuildingsUseCase;
    private final GetMyBuildingUseCase getMyBuildingUseCase;

    @GetMapping("/managers/{managerId}/building")
    public ResponseEntity<BuildingAccessResponse> getManagerBuilding(@PathVariable("managerId") final Long managerId) {

        return getMyBuildingsUseCase.getMyBuildings(managerId)
                                    .stream()
                                    .findFirst()
                                    .map(building -> new BuildingAccessResponse(
                                                    UUID.fromString(building.id()),
                                                    building.buildingName()))
                                    .map(ResponseEntity::ok)
                                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tenants/{tenantUserId}/active-building")
    public BuildingAccessResponse getTenantActiveBuilding(@PathVariable("tenantUserId") final Long tenantUserId) {

        final BuildingInfoResult building = getMyBuildingUseCase.getMyBuilding(tenantUserId);

        return new BuildingAccessResponse(
                        UUID.fromString(building.id()),
                        building.buildingName());
    }
}