package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingMembershipMapper {
    BuildingMembershipEntity toEntity(BuildingMembership membership);

    BuildingMembership toDomain(BuildingMembershipEntity entity);
}
