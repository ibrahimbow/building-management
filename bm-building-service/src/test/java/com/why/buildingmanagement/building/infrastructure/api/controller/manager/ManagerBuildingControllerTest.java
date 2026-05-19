package com.why.buildingmanagement.building.infrastructure.api.controller.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.TenantInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.CreateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.UpdateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.TenantInfoResponse;
import com.why.buildingmanagement.building.infrastructure.api.mapper.BuildingApiMapper;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerBuildingController.class)
class ManagerBuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateBuildingUseCase createBuildingUseCase;

    @MockitoBean
    private GetMyBuildingsUseCase getMyBuildingsUseCase;

    @MockitoBean
    private GetMyBuildingByIdUseCase getMyBuildingByIdUseCase;

    @MockitoBean
    private UpdateMyBuildingUseCase updateMyBuildingUseCase;

    @MockitoBean
    private DeleteMyBuildingUseCase deleteMyBuildingUseCase;

    @MockitoBean
    private GetBuildingTenantsUseCase getBuildingTenantsUseCase;

    @MockitoBean
    private BuildingApiMapper mapper;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private RemoveTenantFromBuildingUseCase removeTenantFromBuildingUseCase;

    @BeforeEach
    void setUp() {
        when(currentUserService.getCurrentUser())
                .thenReturn(currentManager());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createBuilding_shouldCreateBuildingForCurrentManager() throws Exception {
        final CreateBuildingRequest request = new CreateBuildingRequest(
                "Antwerp Residence",
                "Berchem, Antwerp",
                12,
                "+32000000000");

        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(createBuildingUseCase.createBuilding(any(CreateBuildingCommand.class)))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/manager/buildings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/manager/buildings/building-id-1"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"))
                .andExpect(jsonPath("$.managerId").value(1L));

        final ArgumentCaptor<CreateBuildingCommand> captor =
                ArgumentCaptor.forClass(CreateBuildingCommand.class);

        verify(createBuildingUseCase).createBuilding(captor.capture());

        assertThat(captor.getValue().buildingName()).isEqualTo("Antwerp Residence");
        assertThat(captor.getValue().address()).isEqualTo("Berchem, Antwerp");
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
        assertThat(captor.getValue().totalApartments()).isEqualTo(12);
        assertThat(captor.getValue().emergencyPhone()).isEqualTo("+32000000000");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getMyBuildings_shouldReturnCurrentManagersBuildings() throws Exception {
        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(getMyBuildingsUseCase.getMyBuildings(1L))
                .thenReturn(List.of(result));

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/manager/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buildingName").value("Antwerp Residence"))
                .andExpect(jsonPath("$[0].managerId").value(1L));

        verify(getMyBuildingsUseCase).getMyBuildings(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getMyBuildingById_shouldReturnBuildingOwnedByCurrentManager() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(getMyBuildingByIdUseCase.getMyBuildingById(buildingId, 1L))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/manager/buildings/{id}", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"))
                .andExpect(jsonPath("$.managerId").value(1L));

        verify(getMyBuildingByIdUseCase).getMyBuildingById(buildingId, 1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getBuildingTenants_shouldReturnActiveTenantsForManagersBuilding() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        final TenantInfoResult result = createTenantInfoResult();
        final TenantInfoResponse response = createTenantInfoResponse();

        when(getBuildingTenantsUseCase.getBuildingTenants(buildingId, 1L))
                .thenReturn(List.of(result));

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/manager/buildings/{id}/tenants", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantUserId").value(2L))
                .andExpect(jsonPath("$[0].username").value("tenant"))
                .andExpect(jsonPath("$[0].email").value("tenant@example.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("+32470000000"));

        verify(getBuildingTenantsUseCase).getBuildingTenants(buildingId, 1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateMyBuilding_shouldUpdateBuildingOwnedByCurrentManager() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        final UpdateBuildingRequest request = new UpdateBuildingRequest(
                "Updated Residence",
                "Updated Address",
                20,
                "+32111111111");

        final BuildingInfoResult result = new BuildingInfoResult(
                buildingId.toString(),
                "Updated Residence",
                "BM-123456",
                "Updated Address",
                1L,
                "Ibrahim Aref",
                20,
                "+32111111111");

        final BuildingResponse response = new BuildingResponse(
                buildingId.toString(),
                "Updated Residence",
                "BM-123456",
                "Updated Address",
                1L,
                "Ibrahim Aref",
                20,
                "+32111111111");

        when(updateMyBuildingUseCase.updateMyBuilding(any(UpdateMyBuildingCommand.class)))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(put("/api/manager/buildings/{id}", buildingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingName").value("Updated Residence"))
                .andExpect(jsonPath("$.managerId").value(1L));

        final ArgumentCaptor<UpdateMyBuildingCommand> captor =
                ArgumentCaptor.forClass(UpdateMyBuildingCommand.class);

        verify(updateMyBuildingUseCase).updateMyBuilding(captor.capture());

        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
        assertThat(captor.getValue().buildingName()).isEqualTo("Updated Residence");
        assertThat(captor.getValue().address()).isEqualTo("Updated Address");
        assertThat(captor.getValue().totalApartments()).isEqualTo(20);
        assertThat(captor.getValue().emergencyPhone()).isEqualTo("+32111111111");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteMyBuilding_shouldDeleteBuildingOwnedByCurrentManager() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/manager/buildings/{id}", buildingId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        final ArgumentCaptor<DeleteMyBuildingCommand> captor =
                ArgumentCaptor.forClass(DeleteMyBuildingCommand.class);

        verify(deleteMyBuildingUseCase).deleteMyBuilding(captor.capture());

        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void removeTenantFromBuilding_shouldRemoveTenantFromManagersBuilding() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/manager/buildings/{buildingId}/tenants/{tenantUserId}", buildingId, 2L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        final ArgumentCaptor<RemoveTenantFromBuildingCommand> captor =
                ArgumentCaptor.forClass(RemoveTenantFromBuildingCommand.class);

        verify(removeTenantFromBuildingUseCase).removeTenantFromBuilding(captor.capture());

        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().tenantUserId()).isEqualTo(2L);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    private CurrentUser currentManager() {
        return new CurrentUser(
                1L,
                "Ibrahim",
                "ibrahim@example.com",
                "+32000000000",
                "MANAGER");
    }

    private BuildingInfoResult createBuildingInfoResult() {
        return new BuildingInfoResult(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                1L,
                "Ibrahim Aref",
                12,
                "+32000000000");
    }

    private BuildingResponse createBuildingResponse() {
        return new BuildingResponse(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                1L,
                "Ibrahim Aref",
                12,
                "+32000000000");
    }

    private TenantInfoResult createTenantInfoResult() {
        return new TenantInfoResult(
                2L,
                "tenant",
                "tenant@example.com",
                "+32470000000");
    }

    private TenantInfoResponse createTenantInfoResponse() {
        return new TenantInfoResponse(
                2L,
                "tenant",
                "tenant@example.com",
                "+32470000000");
    }
}