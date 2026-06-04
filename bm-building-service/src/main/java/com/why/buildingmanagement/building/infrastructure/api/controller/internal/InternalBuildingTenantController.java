package com.why.buildingmanagement.building.infrastructure.api.controller.internal;

import com.why.buildingmanagement.building.infrastructure.persistence.BuildingRepository;
import com.why.buildingmanagement.building.infrastructure.persistence.BuildingMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/buildings")
@RequiredArgsConstructor
public class InternalBuildingTenantController {

    private final BuildingMembershipRepository buildingMembershipRepository;
    private final BuildingRepository buildingRepository;

    @GetMapping("/{buildingId}/active-tenants")
    public List<BuildingTenantResponse> getActiveTenants(
                    @PathVariable("buildingId") final UUID buildingId) {

        return buildingMembershipRepository
                        .findActiveTenantUserIdsByBuildingId(buildingId)
                        .stream()
                        .map(BuildingTenantResponse::new)
                        .toList();
    }

    @GetMapping("/{buildingId}/manager")
    public BuildingManagerResponse getBuildingManager(
                    @PathVariable("buildingId") final UUID buildingId) {

        final var building = buildingRepository.findById(buildingId)
                        .orElseThrow(() -> new IllegalStateException(
                                        "Building not found: " + buildingId));

        return new BuildingManagerResponse(
                        building.getManagerId());
    }
}