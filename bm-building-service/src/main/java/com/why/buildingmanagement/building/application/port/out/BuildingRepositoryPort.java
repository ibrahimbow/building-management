package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.domain.model.Building;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepositoryPort {

    Building save(Building building);

    Optional<Building> findByCode(String code);

    Optional<Building> findById(UUID id);

    boolean existsByCode(String code);

    Optional<Building> findByManagerId(Long managerId);

    Optional<Building> findByIdAndManagerId(UUID id, Long managerId);

    void delete(Building building);
}