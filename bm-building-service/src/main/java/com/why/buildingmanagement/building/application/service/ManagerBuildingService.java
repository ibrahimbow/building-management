package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.assembler.BuildingInfoAssembler;
import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.TenantInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.TenantNotAssignedToBuildingException;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerBuildingService implements GetMyBuildingsUseCase,
                                               GetMyBuildingByIdUseCase,
                                               UpdateMyBuildingUseCase,
                                               DeleteMyBuildingUseCase,
                                               GetBuildingTenantsUseCase,
                                               RemoveTenantFromBuildingUseCase {

    private final BuildingRepositoryPort buildingRepositoryPort;
    private final BuildingMembershipRepositoryPort membershipRepositoryPort;
    private final BuildingInfoAssembler buildingInfoAssembler;


    @Override
    public List<BuildingInfoResult> getMyBuildings(final Long managerId) {
        return buildingRepositoryPort.findByManagerId(managerId)
                                     .stream()
                                     .map(buildingInfoAssembler::toResult)
                                     .toList();
    }

    @Override
    public BuildingInfoResult getMyBuildingById(final UUID buildingId, final Long managerId) {
        final Building building = findOwnedBuildingOrThrow(buildingId, managerId);

        return buildingInfoAssembler.toResult(building);
    }

    @Override
    @Transactional
    public BuildingInfoResult updateMyBuilding(final UpdateMyBuildingCommand command) {
        final Building building = findOwnedBuildingOrThrow(command.buildingId(), command.managerId());

        final Building updatedBuilding = building.updateDetails(command.buildingName(),
                                                                command.address(),
                                                                command.totalApartments(),
                                                                command.emergencyPhone());

        final Building savedBuilding = buildingRepositoryPort.save(updatedBuilding);

        return buildingInfoAssembler.toResult(savedBuilding);
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


    @Override
    public List<TenantInfoResult> getBuildingTenants(final UUID buildingId, final Long managerId) {

        final Building building = buildingRepositoryPort.findById(buildingId)
                                                        .orElseThrow(() -> new BuildingNotFoundException(buildingId));

        if (!building.isManagedBy(managerId)) {
            throw new BuildingNotFoundException(buildingId);
        }

        return membershipRepositoryPort.findActiveByBuildingId(buildingId)
                                       .stream()
                                       .map(membership -> new TenantInfoResult(
                                                       membership.getTenantUserId(),
                                                       membership.getTenantUsername(),
                                                       membership.getTenantEmail(),
                                                       membership.getTenantPhoneNumber()))
                                       .toList();
    }

    @Override
    @Transactional
    public void removeTenantFromBuilding(final RemoveTenantFromBuildingCommand command) {

        final Building building = buildingRepositoryPort.findById(command.buildingId())
                                                        .orElseThrow(() -> new BuildingNotFoundException(command.buildingId()));

        if (!building.isManagedBy(command.managerId())) {
            throw new BuildingNotFoundException(command.buildingId());
        }

        final BuildingMembership membership = membershipRepositoryPort
                        .findActiveByBuildingIdAndTenantUserId(command.buildingId(), command.tenantUserId())
                        .orElseThrow(() -> new TenantNotAssignedToBuildingException(command.tenantUserId()));

        final BuildingMembership leftMembership = membership.leave();

        membershipRepositoryPort.save(leftMembership);
    }

}