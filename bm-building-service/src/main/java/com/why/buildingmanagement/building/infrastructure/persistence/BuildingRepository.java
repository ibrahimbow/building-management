package com.why.buildingmanagement.building.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Long> {
    Optional<BuildingEntity> findByCode(String code);

    boolean existsByCode(String code);
}
