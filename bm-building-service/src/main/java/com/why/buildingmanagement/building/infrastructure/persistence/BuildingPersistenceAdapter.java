package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuildingPersistenceAdapter implements BuildingRepositoryPort {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;


    @Override
    public Building save(Building building) {
        final BuildingEntity buildingEntity = buildingMapper.toEntity(building);
        final BuildingEntity savedEntity = buildingRepository.save(buildingEntity);
        return buildingMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Building> findByCode(String code) {
        return buildingRepository.findByCode(code)
                .map(buildingMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return buildingRepository.existsByCode(code);
    }
}
