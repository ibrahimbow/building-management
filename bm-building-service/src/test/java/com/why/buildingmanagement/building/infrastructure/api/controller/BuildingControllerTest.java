package com.why.buildingmanagement.building.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.application.port.in.CreateBuildingCommand;
import com.why.buildingmanagement.building.application.port.in.CreateBuildingUseCase;
import com.why.buildingmanagement.building.application.port.in.GetBuildingByCodeUseCase;
import com.why.buildingmanagement.building.application.port.in.JoinBuildingUseCase;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.CreateBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.mapper.BuildingApiMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private BuildingApiMapper buildingApiMapper;

    @Test
    void createBuilding_shouldReturnCreatedBuilding() throws Exception {
        final CreateBuildingRequest request = new CreateBuildingRequest(
                "Antwerp Residence",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000");

        final BuildingInfoResult result = new BuildingInfoResult(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000");

        final BuildingResponse response = new BuildingResponse(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000");


        when(createBuildingUseCase.createBuilding(any()))
                .thenReturn(result);

        when(createBuildingUseCase.createBuilding(any(CreateBuildingCommand.class)))
                .thenReturn(result);

        when(buildingApiMapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("building-id-1"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"))
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.managerEmail").value("ibrahim@example.com"))
                .andExpect(jsonPath("$.totalApartments").value(12));
    }

    @Test
    void getBuildingByCode_shouldReturnBuilding() throws Exception {
        final BuildingInfoResult result = new BuildingInfoResult(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000");

        when(getBuildingByCodeUseCase.getBuildingByCode("BM-123456"))
                .thenReturn(result);

        mockMvc.perform(get("/api/buildings/code/BM-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("building-id-1"))
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));
    }

    @Test
    void joinBuilding_shouldReturnBuildingInfo() throws Exception {
        final JoinBuildingRequest request = new JoinBuildingRequest(
                "BM-123456",
                10L,
                "tenant@example.com");

        final BuildingInfoResult result = new BuildingInfoResult(
                "building-id-1",
                "Antwerp Residence",
                "BM-123456",
                "Berchem, Antwerp",
                "Ibrahim",
                "ibrahim@example.com",
                12,
                "+32000000000");

        when(joinBuildingUseCase.joinBuilding(any()))
                .thenReturn(result);

        mockMvc.perform(post("/api/buildings/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("building-id-1"))
                .andExpect(jsonPath("$.code").value("BM-123456"))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));
    }
}