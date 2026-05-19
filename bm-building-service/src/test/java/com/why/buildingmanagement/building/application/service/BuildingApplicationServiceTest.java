package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.CreateBuildingCommand;
import com.why.buildingmanagement.building.application.port.in.JoinBuildingCommand;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BuildingApplicationServiceTest {

    private BuildingRepositoryPort buildingRepositoryPort;
    private BuildingMembershipRepositoryPort membershipRepositoryPort;
    private BuildingApplicationService buildingApplicationService;

    private LoadManagerInfoPort loadManagerInfoPort;

    @BeforeEach
    void setUp() {
        buildingRepositoryPort = mock(BuildingRepositoryPort.class);
        membershipRepositoryPort = mock(BuildingMembershipRepositoryPort.class);
        loadManagerInfoPort = mock(LoadManagerInfoPort.class);

        when(loadManagerInfoPort.loadManagerInfoById(anyLong()))
                .thenAnswer(invocation -> {
                    final Long managerId = invocation.getArgument(0);

                    return new ManagerInfoResult(
                            managerId,
                            "Ibrahim Aref",
                            "manager@example.com",
                            null);
                });

        buildingApplicationService = new BuildingApplicationService(
                buildingRepositoryPort,
                membershipRepositoryPort,
                loadManagerInfoPort);
    }

    @Test
    void createBuilding_shouldThrowException_whenManagerAlreadyHasBuilding() {
        final CreateBuildingCommand command = new CreateBuildingCommand(
                "Sky Tower",
                "Antwerp Belgium",
                1L,
                20,
                "+32470000000");

        when(buildingRepositoryPort.findByManagerId(1L))
                .thenReturn(Optional.of(existingBuilding()));

        assertThatThrownBy(() -> buildingApplicationService.createBuilding(command))
                .isInstanceOf(ManagerAlreadyHasBuildingException.class)
                .hasMessage("Manager already managed another building: " + existingBuilding().getBuildingName());

        verify(buildingRepositoryPort, never()).save(any(Building.class));
    }


    @Test
    void createBuilding_shouldGenerateUniqueCodeAndSaveBuilding() {
        final CreateBuildingCommand command = new CreateBuildingCommand(
                "Antwerp Residence",
                "Berchem, Antwerp",
                12L,
                8,
                "+32000000000");

        when(buildingRepositoryPort.existsByCode(anyString()))
                .thenReturn(false);

        when(buildingRepositoryPort.save(any(Building.class)))
                .thenAnswer(invocation -> {
                    final Building building = invocation.getArgument(0);

                    return Building.restore(
                            UUID.randomUUID(),
                            building.getBuildingName(),
                            building.getCode(),
                            building.getAddress(),
                            building.getManagerId(),
                            building.getTotalApartments(),
                            building.getEmergencyPhone());
                });

        final BuildingInfoResult result =
                buildingApplicationService.createBuilding(command);

        assertThat(result.id()).isNotBlank();
        assertThat(result.buildingName()).isEqualTo("Antwerp Residence");
        assertThat(result.code()).startsWith("BM-");
        assertThat(result.address()).isEqualTo("Berchem, Antwerp");
        assertThat(result.managerId()).isEqualTo(12L);
        assertThat(result.totalApartments()).isEqualTo(8);
        assertThat(result.emergencyPhone()).isEqualTo("+32000000000");

        verify(buildingRepositoryPort).save(any(Building.class));
    }

    @Test
    void getBuildingByCode_shouldReturnBuildingInfo_whenBuildingExists() {
        final Building building = Building.restore(
                UUID.randomUUID(),
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                19L,
                12,
                "+32470000000");

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        final BuildingInfoResult result =
                buildingApplicationService.getBuildingByCode("BM-123456");

        assertThat(result.code()).isEqualTo("BM-123456");
        assertThat(result.buildingName()).isEqualTo("Antwerp Residence");
        assertThat(result.managerId()).isEqualTo(19L);
    }

    @Test
    void getBuildingByCode_shouldThrowException_whenBuildingDoesNotExist() {
        when(buildingRepositoryPort.findByCode("BM-404"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingApplicationService.getBuildingByCode("BM-404"))
                .isInstanceOf(BuildingNotFoundException.class)
                .hasMessageContaining("BM-404");
    }

    @Test
    void joinBuilding_shouldCreateMembership_whenTenantHasNoActiveMembership() {
        final UUID buildingId = UUID.randomUUID();

        final Building building = Building.restore(
                buildingId,
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                19L,
                12,
                "+32470000000");

        final JoinBuildingCommand command = new JoinBuildingCommand(
                "BM-123456",
                10L,
                "tenantUser",
                "tenant@example.com",
                "+32470000000");

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        when(membershipRepositoryPort.findActiveByTenantUserId(10L))
                .thenReturn(Optional.empty());

        when(membershipRepositoryPort.save(any(BuildingMembership.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final BuildingInfoResult result =
                buildingApplicationService.joinBuilding(command);

        assertThat(result.code()).isEqualTo("BM-123456");

        final ArgumentCaptor<BuildingMembership> captor =
                ArgumentCaptor.forClass(BuildingMembership.class);

        verify(membershipRepositoryPort).save(captor.capture());

        final BuildingMembership savedMembership = captor.getValue();

        assertThat(savedMembership.getBuildingId()).isEqualTo(buildingId);
        assertThat(savedMembership.getTenantUserId()).isEqualTo(10L);
        assertThat(savedMembership.getTenantUsername()).isEqualTo("tenantUser");
        assertThat(savedMembership.getTenantEmail()).isEqualTo("tenant@example.com");
        assertThat(savedMembership.getTenantPhoneNumber()).isEqualTo("+32470000000");
        assertThat(savedMembership.getJoinedAt()).isNotNull();
        assertThat(savedMembership.getLeftAt()).isNull();
    }

    @Test
    void joinBuilding_shouldThrowException_whenTenantAlreadyHasActiveMembership() {
        final UUID buildingId = UUID.randomUUID();

        final Building building = Building.restore(
                buildingId,
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                19L,
                12,
                "+32470000000");

        final JoinBuildingCommand command = new JoinBuildingCommand(
                "BM-123456",
                10L,
                "ibrahim",
                "tenant@example.com",
                "+32470000000");

        final BuildingMembership existingMembership =
                BuildingMembership.createNew(
                        buildingId,
                        10L,
                        "ibrahim",
                        "tenant@example.com",
                        "+32470000000");

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        when(membershipRepositoryPort.findActiveByTenantUserId(10L))
                .thenReturn(Optional.of(existingMembership));

        assertThatThrownBy(() -> buildingApplicationService.joinBuilding(command))
                .isInstanceOf(TenantAlreadyAssignedToBuildingException.class);

        verify(membershipRepositoryPort, never()).save(any());
    }

    private Building existingBuilding() {
        return Building.createNew(
                "Existing Building",
                "BM-111111",
                "Antwerp",
                1L,
                10,
                "+320000000");
    }
}