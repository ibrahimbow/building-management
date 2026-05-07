package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.model.Building;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerBuildingService implements
        GetMyBuildingsUseCase,
        GetMyBuildingByIdUseCase,
        UpdateMyBuildingUseCase,
        DeleteMyBuildingUseCase {

    private final BuildingRepositoryPort buildingRepositoryPort;

    @Override
    public List<BuildingInfoResult> getMyBuildings(final Long managerId) {
        return buildingRepositoryPort.findByManagerId(managerId)
                .stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    public BuildingInfoResult getMyBuildingById(final UUID buildingId, final Long managerId) {
        final Building building = findOwnedBuildingOrThrow(buildingId, managerId);

        return toResult(building);
    }

    @Override
    @Transactional
    public BuildingInfoResult updateMyBuilding(final UpdateMyBuildingCommand command) {
        final Building building = findOwnedBuildingOrThrow(command.buildingId(), command.managerId());

        final Building updatedBuilding = building.updateDetails(
                command.buildingName(),
                command.address(),
                command.totalApartments(),
                command.emergencyPhone());

        final Building savedBuilding = buildingRepositoryPort.save(updatedBuilding);

        return toResult(savedBuilding);
    }

    @Override
    @Transactional
    public void deleteMyBuilding(final DeleteMyBuildingCommand command) {
        final Building building = findOwnedBuildingOrThrow(command.buildingId(), command.managerId());

        buildingRepositoryPort.delete(building);
    }

    private Building findOwnedBuildingOrThrow(final UUID buildingId, final Long managerId) {
        return buildingRepositoryPort.findByIdAndManagerId(buildingId, managerId)
                .orElseThrow(() -> new BuildingNotFoundException(buildingId));
    }

    private BuildingInfoResult toResult(final Building building) {
        return new BuildingInfoResult(
                building.getId() == null ? null : building.getId().toString(),
                building.getBuildingName(),
                building.getCode(),
                building.getAddress(),
                building.getManagerId(),
                building.getTotalApartments(),
                building.getEmergencyPhone()
        );
    }
}