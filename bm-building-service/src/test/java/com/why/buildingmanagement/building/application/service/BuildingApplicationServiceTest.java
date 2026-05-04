package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.in.CreateBuildingCommand;
import com.why.buildingmanagement.building.application.port.in.JoinBuildingCommand;
import com.why.buildingmanagement.building.application.port.out.BuildingMembershipRepositoryPort;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.TenantAlreadyJoinedBuildingException;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.domain.model.BuildingMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildingApplicationServiceTest {

    private BuildingRepositoryPort buildingRepositoryPort;
    private BuildingMembershipRepositoryPort membershipRepositoryPort;
    private BuildingApplicationService buildingApplicationService;

    @BeforeEach
    void setUp() {
        buildingRepositoryPort = mock(BuildingRepositoryPort.class);
        membershipRepositoryPort = mock(BuildingMembershipRepositoryPort.class);

        buildingApplicationService = new BuildingApplicationService(buildingRepositoryPort, membershipRepositoryPort);
    }

    @Test
    void createBuilding_shouldGenerateUniqueCodeAndSaveBuilding() {
        final CreateBuildingCommand command = new CreateBuildingCommand(
                "Antwerp Residence",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000"
        );

        when(buildingRepositoryPort.existsByCode(anyString())).thenReturn(false);

        when(buildingRepositoryPort.save(any(Building.class)))
                .thenAnswer(invocation -> {
                    Building building = invocation.getArgument(0);

                    return Building.builder()
                            .id(UUID.randomUUID())
                            .buildingName(building.getBuildingName())
                            .code(building.getCode())
                            .address(building.getAddress())
                            .managerName(building.getManagerName())
                            .managerEmail(building.getManagerEmail())
                            .totalApartments(building.getTotalApartments())
                            .emergencyPhone(building.getEmergencyPhone())
                            .build();
                });

        final BuildingInfoResult result = buildingApplicationService.createBuilding(command);

        assertThat(result.id()).isNotBlank();
        assertThat(result.buildingName()).isEqualTo("Antwerp Residence");
        assertThat(result.code()).startsWith("BM-");
        assertThat(result.address()).isEqualTo("Berchem, Antwerp");
        assertThat(result.managerEmail()).isEqualTo("ibrahim@example.com");
        assertThat(result.totalApartments()).isEqualTo(12);

        verify(buildingRepositoryPort).save(any(Building.class));
    }

    @Test
    void getBuildingByCode_shouldReturnBuildingInfo_whenBuildingExists() {
        final UUID buildingId = UUID.randomUUID();

        final Building building = Building.builder()
                .id(buildingId)
                .buildingName("Antwerp Residence")
                .code("BM-123456")
                .address("Berchem, Antwerp")
                .managerName("Ibrahim")
                .managerEmail("ibrahim@example.com")
                .totalApartments(12)
                .emergencyPhone("+32470000000")
                .build();

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        final BuildingInfoResult result = buildingApplicationService.getBuildingByCode("BM-123456");

        assertThat(result.id()).isEqualTo(buildingId.toString());
        assertThat(result.code()).isEqualTo("BM-123456");
        assertThat(result.buildingName()).isEqualTo("Antwerp Residence");
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
    void joinBuilding_shouldCreateMembership_whenTenantHasNotJoinedBefore() {
        final UUID buildingId = UUID.randomUUID();

        final Building building = Building.builder()
                .id(buildingId)
                .buildingName("Antwerp Residence")
                .code("BM-123456")
                .address("Berchem, Antwerp")
                .managerName("Ibrahim")
                .managerEmail("ibrahim@example.com")
                .totalApartments(12)
                .emergencyPhone("+32470000000")
                .build();

        final JoinBuildingCommand command = new JoinBuildingCommand(
                "BM-123456",
                10L,
                "tenant@example.com"
        );

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        when(membershipRepositoryPort.existsByBuildingIdAndTenantUserId(buildingId, 10L))
                .thenReturn(false);

        when(membershipRepositoryPort.save(any(BuildingMembership.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final BuildingInfoResult result = buildingApplicationService.joinBuilding(command);

        assertThat(result.id()).isEqualTo(buildingId.toString());
        assertThat(result.code()).isEqualTo("BM-123456");

        final ArgumentCaptor<BuildingMembership> captor =
                ArgumentCaptor.forClass(BuildingMembership.class);

        verify(membershipRepositoryPort).save(captor.capture());

        assertThat(captor.getValue().getBuildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().getTenantUserId()).isEqualTo(10L);
        assertThat(captor.getValue().getTenantEmail()).isEqualTo("tenant@example.com");
    }

    @Test
    void joinBuilding_shouldThrowException_whenTenantAlreadyJoined() {
        final UUID buildingId = UUID.randomUUID();

        final Building building = Building.builder()
                .id(buildingId)
                .buildingName("Antwerp Residence")
                .code("BM-123456")
                .address("Berchem, Antwerp")
                .managerName("Ibrahim")
                .managerEmail("ibrahim@example.com")
                .totalApartments(12)
                .emergencyPhone("+32470000000")
                .build();

        final JoinBuildingCommand command = new JoinBuildingCommand(
                "BM-123456",
                10L,
                "tenant@example.com"
        );

        when(buildingRepositoryPort.findByCode("BM-123456"))
                .thenReturn(Optional.of(building));

        when(membershipRepositoryPort.existsByBuildingIdAndTenantUserId(buildingId, 10L))
                .thenReturn(true);

        assertThatThrownBy(() -> buildingApplicationService.joinBuilding(command))
                .isInstanceOf(TenantAlreadyJoinedBuildingException.class);

        verify(membershipRepositoryPort, never()).save(any());
    }
}