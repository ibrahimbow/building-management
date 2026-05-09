package com.why.buildingmanagement.building.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingMembershipRepository extends JpaRepository<BuildingMembershipEntity, UUID> {

    boolean existsByBuildingIdAndTenantUserIdAndLeftAtIsNull(UUID buildingId, Long tenantUserId);

    Optional<BuildingMembershipEntity> findByTenantUserIdAndLeftAtIsNull(Long tenantUserId);

    List<BuildingMembershipEntity> findByBuildingIdAndLeftAtIsNull(UUID buildingId);

    Optional<BuildingMembershipEntity> findByBuildingIdAndTenantUserIdAndLeftAtIsNull(UUID buildingId, Long tenantUserId);
}