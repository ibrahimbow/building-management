package com.why.buildingmanagement.building.infrastructure.api.controller.internal;

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

    @GetMapping("/{buildingId}/active-tenants")
    public List<BuildingTenantResponse> getActiveTenants(
                    @PathVariable("buildingId") final UUID buildingId) {

        return buildingMembershipRepository
                        .findActiveTenantUserIdsByBuildingId(buildingId)
                        .stream()
                        .map(BuildingTenantResponse::new)
                        .toList();
    }

    public record BuildingTenantResponse(
                    Long userId
    ) {
    }
}