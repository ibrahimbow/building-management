package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.domain.model.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingRepositoryPort {

    Building save(Building building);

    Optional<Building> findByCode(String code);

    boolean existsByCode(String code);

    List<Building> findByManagerId(Long managerId);
}
