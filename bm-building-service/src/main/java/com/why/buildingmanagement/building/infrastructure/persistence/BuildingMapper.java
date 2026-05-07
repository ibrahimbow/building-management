package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.domain.model.Building;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    default BuildingEntity toEntity(final Building building) {
        if (building == null) {
            return null;
        }

        return new BuildingEntity(
                building.getId(),
                building.getBuildingName(),
                building.getCode(),
                building.getAddress(),
                building.getManagerId(),
                building.getTotalApartments(),
                building.getEmergencyPhone()
        );
    }

    default Building toDomain(final BuildingEntity entity) {
        if (entity == null) {
            return null;
        }

        return Building.restore(
                entity.getId(),
                entity.getBuildingName(),
                entity.getCode(),
                entity.getAddress(),
                entity.getManagerId(),
                entity.getTotalApartments(),
                entity.getEmergencyPhone()
        );
    }
}