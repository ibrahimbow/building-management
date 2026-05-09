package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingMembershipMapper {

    default BuildingMembershipEntity toEntity(final BuildingMembership membership) {

        return new BuildingMembershipEntity(
                membership.getId(),
                membership.getBuildingId(),
                membership.getTenantUserId(),
                membership.getTenantUsername(),
                membership.getTenantEmail(),
                membership.getTenantPhoneNumber(),
                membership.getJoinedAt(),
                membership.getLeftAt()
        );
    }

    default BuildingMembership toDomain(final BuildingMembershipEntity entity) {

        return BuildingMembership.restore(
                entity.getId(),
                entity.getBuildingId(),
                entity.getTenantUserId(),
                entity.getTenantUsername(),
                entity.getTenantEmail(),
                entity.getTenantPhoneNumber(),
                entity.getJoinedAt(),
                entity.getLeftAt()
        );
    }
}