package com.why.buildingmanagement.building.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<BuildingEntity, UUID> {

    Optional<BuildingEntity> findByCode(String code);

    boolean existsByCode(String code);

    List<BuildingEntity> findByManagerId(Long managerId);
}