package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingPersistenceAdapter implements BuildingRepositoryPort {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    public Building save(final Building building) {
        final BuildingEntity buildingEntity = buildingMapper.toEntity(building);
        final BuildingEntity savedEntity = buildingRepository.save(buildingEntity);
        return buildingMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Building> findByCode(final String code) {
        return buildingRepository.findByCode(code)
                .map(buildingMapper::toDomain);
    }

    @Override
    public Optional<Building> findById(UUID id) {
        return buildingRepository.findById(id)
                .map(buildingMapper::toDomain);
    }

    @Override
    public boolean existsByCode(final String code) {
        return buildingRepository.existsByCode(code);
    }

    @Override
    public List<Building> findByManagerId(final Long managerId) {

        return buildingRepository.findByManagerId(managerId)
                .stream()
                .map(buildingMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Building> findByIdAndManagerId(final UUID id, final Long managerId) {

        return buildingRepository.findByIdAndManagerId(id, managerId)
                .map(buildingMapper::toDomain);
    }

    @Override
    public void delete(final Building building) {
        buildingRepository.deleteById(building.getId());
    }
}