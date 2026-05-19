package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.LoadManagerInfoPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.ManagerInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.ManagerAlreadyHasBuildingException;
import com.why.buildingmanagement.building.domain.exception.TenantAlreadyAssignedToBuildingException;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BuildingApplicationService implements
        CreateBuildingUseCase,
        GetBuildingByCodeUseCase,
        JoinBuildingUseCase {

    private final BuildingRepositoryPort buildingRepositoryPort;
    private final BuildingMembershipRepositoryPort buildingMembershipRepositoryPort;
    private final LoadManagerInfoPort loadManagerInfoPort;


    @Override
    public BuildingInfoResult createBuilding(final CreateBuildingCommand command) {
        buildingRepositoryPort.findByManagerId(command.managerId())
                .ifPresent(building -> {
                    throw new ManagerAlreadyHasBuildingException(building.getBuildingName());
                });

        final String buildingCode = generateUniqueCode();

        final Building building = Building.createNew(
                command.buildingName(),
                buildingCode,
                command.address(),
                command.managerId(),
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

    @Override
    public BuildingInfoResult joinBuilding(final JoinBuildingCommand command) {

        final Building building = buildingRepositoryPort.findByCode(command.code())
                .orElseThrow(() -> new BuildingNotFoundException(command.code()));

        buildingMembershipRepositoryPort.findActiveByTenantUserId(command.tenantUserId())
                .ifPresent(membership -> {
                    throw new TenantAlreadyAssignedToBuildingException(command.tenantUserId());
                });

        final BuildingMembership buildingMembership = BuildingMembership.createNew(
                building.getId(),
                command.tenantUserId(),
                command.tenantUsername(),
                command.tenantEmail(),
                command.tenantPhoneNumber());

        buildingMembershipRepositoryPort.save(buildingMembership);

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

        final ManagerInfoResult managerInfo = loadManagerInfoPort.loadManagerInfoById(
                building.getManagerId());

        return new BuildingInfoResult(
                building.getId() == null ? null : building.getId().toString(),
                building.getBuildingName(),
                building.getCode(),
                building.getAddress(),
                building.getManagerId(),
                managerInfo.displayName(),
                building.getTotalApartments(),
                building.getEmergencyPhone());
    }

}