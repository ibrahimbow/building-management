package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.domain.model.BuildingMembership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingMembershipRepositoryPort {

    BuildingMembership save(BuildingMembership membership);

    Optional<BuildingMembership> findActiveByTenantUserId(Long tenantUserId);

    List<BuildingMembership> findActiveByBuildingId(UUID buildingId);

    Optional<BuildingMembership> findActiveByBuildingIdAndTenantUserId(UUID buildingId, Long tenantUserId);
}