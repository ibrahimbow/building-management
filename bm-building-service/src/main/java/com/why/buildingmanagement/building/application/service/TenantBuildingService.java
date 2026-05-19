package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.GetMyBuildingUseCase;
import com.why.buildingmanagement.building.application.port.in.LeaveBuildingCommand;
import com.why.buildingmanagement.building.application.port.in.LeaveBuildingUseCase;
import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.LoadManagerInfoPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.ManagerInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.TenantNotAssignedToBuildingException;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantBuildingService implements
        GetMyBuildingUseCase,
        LeaveBuildingUseCase {

    private final BuildingRepositoryPort buildingRepositoryPort;
    private final BuildingMembershipRepositoryPort membershipRepositoryPort;
    private final LoadManagerInfoPort loadManagerInfoPort;

    @Override
    public BuildingInfoResult getMyBuilding(final Long tenantUserId) {
        final BuildingMembership membership = findActiveMembershipOrThrow(tenantUserId);

        final Building building = buildingRepositoryPort
                .findById(membership.getBuildingId())
                .orElseThrow(() -> new BuildingNotFoundException(membership.getBuildingId()));

        return toResult(building);
    }

    @Override
    @Transactional
    public void leaveBuilding(final LeaveBuildingCommand command) {
        final BuildingMembership membership =
                findActiveMembershipOrThrow(command.tenantUserId());

        membership.leave();

        membershipRepositoryPort.save(membership);
    }

    private BuildingMembership findActiveMembershipOrThrow(final Long tenantUserId) {
        return membershipRepositoryPort
                .findActiveByTenantUserId(tenantUserId)
                .orElseThrow(() -> new TenantNotAssignedToBuildingException(tenantUserId));
    }

    private BuildingInfoResult toResult(final Building building) {

        final ManagerInfoResult managerInfo =
                loadManagerInfoPort.loadManagerInfoById(
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