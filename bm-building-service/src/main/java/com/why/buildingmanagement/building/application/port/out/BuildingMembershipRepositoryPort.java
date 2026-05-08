package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.domain.model.BuildingMembership;

import java.util.Optional;
import java.util.UUID;

public interface BuildingMembershipRepositoryPort {

    BuildingMembership save(BuildingMembership membership);

    boolean existsByBuildingIdAndTenantUserId(UUID buildingId, Long tenantUserId);

    Optional<BuildingMembership> findByTenantUserId(Long tenantUserId);

    void delete(BuildingMembership membership);
}