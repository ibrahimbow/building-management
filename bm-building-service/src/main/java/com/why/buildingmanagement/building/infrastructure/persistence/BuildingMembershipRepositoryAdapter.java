package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BuildingMembershipRepositoryAdapter implements BuildingMembershipRepositoryPort {

    private final BuildingMembershipRepository repository;
    private final BuildingMembershipMapper mapper;

    @Override
    public BuildingMembership save(final BuildingMembership membership) {
        return mapper.toDomain(repository.save(mapper.toEntity(membership)));
    }

    @Override
    public Optional<BuildingMembership> findActiveByTenantUserId(final Long tenantUserId) {
        return repository.findByTenantUserIdAndLeftAtIsNull(tenantUserId)
                .map(mapper::toDomain);
    }

    @Override
    public List<BuildingMembership> findActiveByBuildingId(final UUID buildingId) {
        return repository.findByBuildingIdAndLeftAtIsNull(buildingId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BuildingMembership> findActiveByBuildingIdAndTenantUserId(final UUID buildingId,
                                                                              final Long tenantUserId) {

        return repository.findByBuildingIdAndTenantUserIdAndLeftAtIsNull(buildingId, tenantUserId)
                .map(mapper::toDomain);
    }
}