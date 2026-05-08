package com.why.buildingmanagement.building.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingMembershipRepository extends JpaRepository<BuildingMembershipEntity, UUID> {

    boolean existsByBuildingIdAndTenantUserId(UUID buildingId, Long tenantUserId);

    Optional<BuildingMembershipEntity> findByTenantUserId(Long tenantUserId);

    Optional<BuildingMembershipEntity> findByBuildingIdAndTenantUserId(UUID buildingId, Long tenantUserId);
}