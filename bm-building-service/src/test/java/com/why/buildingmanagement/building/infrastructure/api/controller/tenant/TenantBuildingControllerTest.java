package com.why.buildingmanagement.building.infrastructure.api.controller.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.application.port.in.*;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantBuildingController.class)
class TenantBuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JoinBuildingUseCase joinBuildingUseCase;

    @MockitoBean
    private GetBuildingByCodeUseCase getBuildingByCodeUseCase;

    @MockitoBean
    private GetMyBuildingUseCase getMyBuildingUseCase;

    @MockitoBean
    private LeaveBuildingUseCase leaveBuildingUseCase;

    @MockitoBean
    private BuildingApiMapper mapper;

    @MockitoBean
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        10L,
                        "Tenant",
                        "tenant@example.com",
                        "+32000000000",
                        "TENANT"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void joinBuilding_shouldReturnCreatedBuilding() throws Exception {
        final JoinBuildingRequest request =
                new JoinBuildingRequest(
                        "BM-123456");

        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(joinBuildingUseCase.joinBuilding(any(JoinBuildingCommand.class)))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tenant/buildings/my-building"))
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));

        final ArgumentCaptor<JoinBuildingCommand> captor =
                ArgumentCaptor.forClass(JoinBuildingCommand.class);

        verify(joinBuildingUseCase).joinBuilding(captor.capture());

        assertThat(captor.getValue().code()).isEqualTo("BM-123456");
        assertThat(captor.getValue().tenantUserId()).isEqualTo(10L);
        assertThat(captor.getValue().tenantEmail()).isEqualTo("tenant@example.com");
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void getBuildingByCode_shouldReturnBuilding() throws Exception {
        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(getBuildingByCodeUseCase.getBuildingByCode("BM-123456"))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/tenant/buildings/code/BM-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));

    }

    @Test
    @WithMockUser(roles = "TENANT")
    void getMyBuilding_shouldReturnCurrentTenantsBuilding() throws Exception {
        final BuildingInfoResult result = createBuildingInfoResult();
        final BuildingResponse response = createBuildingResponse();

        when(getMyBuildingUseCase.getMyBuilding(10L))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/tenant/buildings/my-building"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"))
                .andExpect(jsonPath("$.code").value("BM-123456"));

        verify(getMyBuildingUseCase).getMyBuilding(10L);
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void leaveBuilding_shouldLeaveCurrentTenantsBuilding() throws Exception {
        mockMvc.perform(post("/api/tenant/buildings/my-building/leave")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        final ArgumentCaptor<LeaveBuildingCommand> captor =
                ArgumentCaptor.forClass(LeaveBuildingCommand.class);

        verify(leaveBuildingUseCase).leaveBuilding(captor.capture());

        assertThat(captor.getValue().tenantUserId()).isEqualTo(10L);
    }

    private BuildingInfoResult createBuildingInfoResult() {
        return new BuildingInfoResult(
                "1",
                "Antwerp Residence",
                "BM-123456",
                "Antwerp",
                12L,
                10,
                "+320000000");
    }

    private BuildingResponse createBuildingResponse() {
        return new BuildingResponse(
                "1",
                "Antwerp Residence",
                "BM-123456",
                "Antwerp",
                12L,
                10,
                "+320000000");
    }
}