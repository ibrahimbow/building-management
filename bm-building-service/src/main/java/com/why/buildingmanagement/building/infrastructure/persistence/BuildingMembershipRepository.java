package com.why.buildingmanagement.building.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingMembershipRepository extends JpaRepository<BuildingMembershipEntity, UUID> {

    Optional<BuildingMembershipEntity> findByTenantUserIdAndLeftAtIsNull(Long tenantUserId);

    List<BuildingMembershipEntity> findByBuildingIdAndLeftAtIsNull(UUID buildingId);

    Optional<BuildingMembershipEntity> findByBuildingIdAndTenantUserIdAndLeftAtIsNull(UUID buildingId, Long tenantUserId);

    @Query("""
        SELECT membership.tenantUserId
        FROM BuildingMembershipEntity membership
        WHERE membership.buildingId = :buildingId
        AND membership.leftAt IS NULL
        """)
    List<Long> findActiveTenantUserIdsByBuildingId(
                    @Param("buildingId") UUID buildingId);
}