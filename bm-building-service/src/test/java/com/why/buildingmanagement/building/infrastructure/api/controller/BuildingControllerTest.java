package com.why.buildingmanagement.building.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.CreateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.UpdateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
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

@WebMvcTest(BuildingController.class)
class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateBuildingUseCase createBuildingUseCase;

    @MockitoBean
    private GetBuildingByCodeUseCase getBuildingByCodeUseCase;

    @MockitoBean
    private JoinBuildingUseCase joinBuildingUseCase;

    @MockitoBean
    private GetMyBuildingsUseCase getMyBuildingsUseCase;

    @MockitoBean
    private GetMyBuildingByIdUseCase getMyBuildingByIdUseCase;

    @MockitoBean
    private UpdateMyBuildingUseCase updateMyBuildingUseCase;

    @MockitoBean
    private DeleteMyBuildingUseCase deleteMyBuildingUseCase;

    @MockitoBean
    private BuildingApiMapper buildingApiMapper;

    @MockitoBean
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        when(currentUserService.getCurrentUser())
                .thenReturn(currentManager(1L));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createBuilding_shouldUseCurrentUserIdAsManagerId() throws Exception {
        final CreateBuildingRequest request = new CreateBuildingRequest(
                "Antwerp Residence",
                "Berchem, Antwerp",
                12,
                "+32000000000"
        );

        final BuildingInfoResult result = createBuildingInfoResult(1L);
        final BuildingResponse response = createBuildingResponse(1L);

        when(createBuildingUseCase.createBuilding(any(CreateBuildingCommand.class)))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/buildings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.managerId").value(1L))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));

        final ArgumentCaptor<CreateBuildingCommand> captor =
                ArgumentCaptor.forClass(CreateBuildingCommand.class);

        verify(createBuildingUseCase).createBuilding(captor.capture());

        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getMyBuildings_shouldReturnOnlyCurrentManagersBuildings() throws Exception {
        final BuildingInfoResult result = createBuildingInfoResult(1L);
        final BuildingResponse response = createBuildingResponse(1L);

        when(getMyBuildingsUseCase.getMyBuildings(1L))
                .thenReturn(List.of(result));

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/buildings/managed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].managerId").value(1L))
                .andExpect(jsonPath("$[0].buildingName").value("Antwerp Residence"));

        verify(getMyBuildingsUseCase).getMyBuildings(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getMyBuildingById_shouldUseCurrentManagerId() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        final BuildingInfoResult result = createBuildingInfoResult(1L);
        final BuildingResponse response = createBuildingResponse(1L);

        when(getMyBuildingByIdUseCase.getMyBuildingById(buildingId, 1L))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/buildings/{id}", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(1L))
                .andExpect(jsonPath("$.code").value("BM-123456"));

        verify(getMyBuildingByIdUseCase).getMyBuildingById(buildingId, 1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getMyBuildingById_shouldReturn404_whenBuildingNotFound() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        when(getMyBuildingByIdUseCase.getMyBuildingById(buildingId, 1L))
                .thenThrow(new BuildingNotFoundException(buildingId));

        mockMvc.perform(get("/api/buildings/{id}", buildingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Building not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createBuilding_shouldReturn400_whenRequestIsInvalid() throws Exception {
        final CreateBuildingRequest request = new CreateBuildingRequest(
                "",
                "",
                12,
                "");

        mockMvc.perform(post("/api/buildings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateMyBuilding_shouldUseCurrentManagerId() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        final UpdateBuildingRequest request = new UpdateBuildingRequest(
                "Updated Residence",
                "Updated Address",
                20,
                "+32111111111"
        );

        final BuildingInfoResult result = new BuildingInfoResult(
                buildingId.toString(),
                "Updated Residence",
                "BM-123456",
                "Updated Address",
                1L,
                20,
                "+32111111111"
        );

        final BuildingResponse response = new BuildingResponse(
                buildingId.toString(),
                "Updated Residence",
                "BM-123456",
                "Updated Address",
                1L,
                20,
                "+32111111111"
        );

        when(updateMyBuildingUseCase.updateMyBuilding(any(UpdateMyBuildingCommand.class)))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(put("/api/buildings/{id}", buildingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(1L))
                .andExpect(jsonPath("$.buildingName").value("Updated Residence"));

        final ArgumentCaptor<UpdateMyBuildingCommand> captor =
                ArgumentCaptor.forClass(UpdateMyBuildingCommand.class);

        verify(updateMyBuildingUseCase).updateMyBuilding(captor.capture());

        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteMyBuilding_shouldUseCurrentManagerId() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/buildings/{id}", buildingId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        final ArgumentCaptor<DeleteMyBuildingCommand> captor =
                ArgumentCaptor.forClass(DeleteMyBuildingCommand.class);

        verify(deleteMyBuildingUseCase).deleteMyBuilding(captor.capture());

        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void getBuildingByCode_shouldReturnBuilding() throws Exception {
        final BuildingInfoResult result = createBuildingInfoResult(12L);
        final BuildingResponse response = createBuildingResponse(12L);

        when(getBuildingByCodeUseCase.getBuildingByCode("BM-123456"))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/buildings/code/BM-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void joinBuilding_shouldReturnBuildingInfo() throws Exception {
        final JoinBuildingRequest request = new JoinBuildingRequest(
                "BM-123456",
                10L,
                "tenant@example.com"
        );

        final BuildingInfoResult result = createBuildingInfoResult(12L);
        final BuildingResponse response = createBuildingResponse(12L);

        when(joinBuildingUseCase.joinBuilding(any(JoinBuildingCommand.class)))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));
    }

    private CurrentUser currentManager(final Long managerId) {
        return new CurrentUser(
                managerId,
                "Ibrahim",
                "ibrahim@example.com",
                "MANAGER"
        );
    }

    private BuildingInfoResult createBuildingInfoResult(final Long managerId) {
        return new BuildingInfoResult(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                managerId,
                12,
                "+32000000000"
        );
    }

    private BuildingResponse createBuildingResponse(final Long managerId) {
        return new BuildingResponse(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                managerId,
                12,
                "+32000000000"
        );
    }
}