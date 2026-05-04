package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.TenantAlreadyJoinedBuildingException;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BuildingApplicationService implements CreateBuildingUseCase, GetBuildingByCodeUseCase, JoinBuildingUseCase {

    private final BuildingRepositoryPort buildingRepositoryPort;
    private final BuildingMembershipRepositoryPort buildingMembershipRepositoryPort;

    @Override
    public BuildingInfoResult createBuilding(final CreateBuildingCommand command) {
        final String buildingCode = generateUniqueCode();

        final Building building = Building.createNew(
                command.buildingName(),
                buildingCode,
                command.address(),
                command.managerName(),
                command.managerEmail(),
                command.totalApartments(),
                command.emergencyPhone());

        final Building savedBuilding = buildingRepositoryPort.save(building);

        return toBuildingInfoResult(savedBuilding);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingInfoResult getBuildingByCode(final String code) {
        final Building building = buildingRepositoryPort.findByCode(code)
                .orElseThrow(() -> new BuildingNotFoundException(code));

        return toBuildingInfoResult(building);
    }

    private String generateUniqueCode() {
        String code;

        do {
            code = Building.generateCode();
        } while (buildingRepositoryPort.existsByCode(code));

        return code;
    }

    private BuildingInfoResult toBuildingInfoResult(final Building building) {
        return new BuildingInfoResult(
                building.getId() == null ? null : building.getId().toString(),
                building.getBuildingName(),
                building.getCode(),
                building.getAddress(),
                building.getManagerName(),
                building.getManagerEmail(),
                building.getTotalApartments(),
                building.getEmergencyPhone());
    }

    @Override
    public BuildingInfoResult joinBuilding(JoinBuildingCommand command) {
        final Building building = buildingRepositoryPort.findByCode(command.code())
                .orElseThrow(() -> new BuildingNotFoundException(command.code()));
        final boolean alreadyJoined = buildingMembershipRepositoryPort
                .existsByBuildingIdAndTenantUserId(building.getId(), command.tenantUserId());

        if (alreadyJoined) {
            throw new TenantAlreadyJoinedBuildingException(building.getId(), command.tenantUserId());
        }

        final BuildingMembership buildingMembership = BuildingMembership.createNew(
                building.getId(),
                command.tenantUserId(),
                command.tenantEmail());

        buildingMembershipRepositoryPort.save(buildingMembership);

        return toBuildingInfoResult(building);
    }
}