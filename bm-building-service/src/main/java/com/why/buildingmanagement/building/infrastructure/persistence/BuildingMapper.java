package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.domain.model.Building;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    BuildingEntity toEntity(Building building);

    Building toDomain(BuildingEntity entity);
}