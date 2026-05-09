package com.why.buildingmanagement.building.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.BuildingServiceApplication;
import com.why.buildingmanagement.building.domain.model.Building;
import com.why.buildingmanagement.building.infrastructure.api.dto.request.JoinBuildingRequest;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.building.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BuildingServiceApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TenantBuildingLifecycleIntegrationTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withDatabaseName("building_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configure(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingMapper buildingMapper;

    @MockitoBean
    private CurrentUserService currentUserService;

    private static final Long TENANT_ID = 2L;
    private static final String BUILDING_CODE = "BM-751788";
    private static final String SECOND_BUILDING_CODE = "BM-999111";

    @BeforeEach
    void setUp() {
        reset(currentUserService);

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        TENANT_ID,
                        "tenant",
                        "tenant@example.com",
                        "+32000000000",
                        "TENANT"));

        final Building building = Building.createNew(
                "Antwerp Residence",
                BUILDING_CODE,
                "Berchem, Antwerp",
                1L,
                12,
                "+32000000000");

        final Building secondBuilding = Building.createNew(
                "Brussels Residence",
                SECOND_BUILDING_CODE,
                "Brussels",
                1L,
                20,
                "+32111111111");

        buildingRepository.save(buildingMapper.toEntity(building));
        buildingRepository.save(buildingMapper.toEntity(secondBuilding));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void tenantLifecycle_shouldJoinViewLeaveAndThenReturn404() throws Exception {
        final JoinBuildingRequest joinRequest = new JoinBuildingRequest(BUILDING_CODE);

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(BUILDING_CODE));

        mockMvc.perform(get("/api/tenant/buildings/my-building"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(BUILDING_CODE))
                .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));

        mockMvc.perform(post("/api/tenant/buildings/my-building/leave")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tenant/buildings/my-building"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void tenantCannotJoinTwice_shouldReturnConflict() throws Exception {
        final JoinBuildingRequest joinRequest = new JoinBuildingRequest(BUILDING_CODE);

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void tenantCannotJoinAnotherBuildingWhileActive_shouldReturnConflict() throws Exception {
        final JoinBuildingRequest firstJoinRequest = new JoinBuildingRequest(BUILDING_CODE);
        final JoinBuildingRequest secondJoinRequest = new JoinBuildingRequest(SECOND_BUILDING_CODE);

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstJoinRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondJoinRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void tenantCanJoinAgainAfterLeaving_shouldReturnCreated() throws Exception {
        final JoinBuildingRequest firstJoinRequest = new JoinBuildingRequest(BUILDING_CODE);
        final JoinBuildingRequest secondJoinRequest = new JoinBuildingRequest(SECOND_BUILDING_CODE);

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstJoinRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/my-building/leave")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondJoinRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(SECOND_BUILDING_CODE));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerRemovesTenant_shouldPreventTenantFromAccessingBuilding() throws Exception {
        final JoinBuildingRequest joinRequest =
                new JoinBuildingRequest(BUILDING_CODE);

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        TENANT_ID,
                        "tenant",
                        "tenant@example.com",
                        "+32000000000",
                        "TENANT"));

        mockMvc.perform(post("/api/tenant/buildings/join")
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isCreated());

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        1L,
                        "manager",
                        "manager@example.com",
                        "+32000000000",
                        "MANAGER"));

        final UUID buildingId = buildingRepository.findByCode(BUILDING_CODE).orElseThrow().getId();

        mockMvc.perform(delete("/api/manager/buildings/{id}/tenants/{tenantUserId}", buildingId, TENANT_ID)
                        .with(user("manager").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        TENANT_ID,
                        "tenant",
                        "tenant@example.com",
                        "+32000000000",
                        "TENANT"));

        mockMvc.perform(get("/api/tenant/buildings/my-building")
                        .with(user("tenant").roles("TENANT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}