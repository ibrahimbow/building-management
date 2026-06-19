package com.why.buildingmanagement.building.infrastructure.api.controller.internal;

import com.why.buildingmanagement.building.infrastructure.api.dto.response.TenantBuildingResponse;
import com.why.buildingmanagement.building.infrastructure.persistence.BuildingMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/tenants")
@RequiredArgsConstructor
public class InternalTenantBuildingController {

    private final BuildingMembershipRepository buildingMembershipRepository;

    @GetMapping("/{tenantUserId}/building")
    public TenantBuildingResponse getActiveBuilding(@PathVariable("tenantUserId") final Long tenantUserId) {

        final var membership = buildingMembershipRepository.findByTenantUserIdAndLeftAtIsNull(tenantUserId)
                                                           .orElseThrow(() -> new IllegalStateException(
                                                                           "Tenant has no active building."));

        return new TenantBuildingResponse(membership.getBuildingId());
    }
}