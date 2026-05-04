package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingMembershipPersistenceAdapter implements BuildingMembershipRepositoryPort {

    private final BuildingMembershipRepository buildingMembershipRepository;
    private final BuildingMembershipMapper mapper;

    @Override
    public BuildingMembership save(final BuildingMembership membership) {
        final BuildingMembershipEntity entity = mapper.toEntity(membership);
        final BuildingMembershipEntity savedEntity = buildingMembershipRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByBuildingIdAndTenantUserId(final UUID buildingId, final Long tenantUserId) {
        return buildingMembershipRepository.existsByBuildingIdAndTenantUserId(buildingId, tenantUserId);
    }
}