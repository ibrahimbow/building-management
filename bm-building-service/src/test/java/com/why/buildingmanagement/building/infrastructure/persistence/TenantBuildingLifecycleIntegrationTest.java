package com.why.buildingmanagement.building.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.building.BuildingServiceApplication;
import com.why.buildingmanagement.building.application.port.out.LoadManagerInfoPort;
import com.why.buildingmanagement.building.application.result.ManagerInfoResult;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BuildingServiceApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TenantBuildingLifecycleIntegrationTest {

    private static final Long MANAGER_ID = 1L;
    private static final Long TENANT_ID = 2L;
    private static final Long SECOND_MANAGER_ID = 3L;

    private static final String BUILDING_CODE = "BM-751788";
    private static final String SECOND_BUILDING_CODE = "BM-999111";

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

    @MockitoBean
    private LoadManagerInfoPort loadManagerInfoPort;

    @BeforeEach
    void setUp() {
        reset(currentUserService, loadManagerInfoPort);

        when(loadManagerInfoPort.loadManagerInfoById(anyLong()))
                        .thenAnswer(invocation -> {
                            final Long managerId = invocation.getArgument(0);

                            return new ManagerInfoResult(
                                            managerId,
                                            "Manager " + managerId,
                                            "manager" + managerId + "@example.com",
                                            null);
                        });

        buildingRepository.save(buildingMapper.toEntity(Building.createNew(
                        "Antwerp Residence",
                        BUILDING_CODE,
                        "Berchem, Antwerp",
                        MANAGER_ID,
                        12,
                        "+32000000000")));

        buildingRepository.save(buildingMapper.toEntity(Building.createNew(
                        "Brussels Residence",
                        SECOND_BUILDING_CODE,
                        "Brussels",
                        SECOND_MANAGER_ID,
                        20,
                        "+32111111111")));
    }

    @Test
    void tenantShouldJoinViewLeaveAndThenNoLongerHaveActiveBuilding() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(BUILDING_CODE))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.code").value(BUILDING_CODE));

        mockMvc.perform(get("/api/tenant/buildings/my-building")
                                        .with(user("tenant").roles("TENANT")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(BUILDING_CODE))
                        .andExpect(jsonPath("$.buildingName").value("Antwerp Residence"));

        mockMvc.perform(post("/api/tenant/buildings/my-building/leave")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf()))
                        .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tenant/buildings/my-building")
                                        .with(user("tenant").roles("TENANT")))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void tenantShouldNotJoinSameBuildingTwiceWhileMembershipIsActive() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        final JoinBuildingRequest request =
                        new JoinBuildingRequest(BUILDING_CODE);

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isConflict());
    }

    @Test
    void tenantShouldNotJoinAnotherBuildingWhileMembershipIsActive() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(BUILDING_CODE))))
                        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(SECOND_BUILDING_CODE))))
                        .andExpect(status().isConflict());
    }

    @Test
    void tenantShouldJoinAnotherBuildingAfterLeavingCurrentBuilding() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(BUILDING_CODE))))
                        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tenant/buildings/my-building/leave")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf()))
                        .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(SECOND_BUILDING_CODE))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.code").value(SECOND_BUILDING_CODE));
    }

    @Test
    void managerShouldRemoveTenantAndTenantShouldNoLongerAccessBuilding() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        mockMvc.perform(post("/api/tenant/buildings/join")
                                        .with(user("tenant").roles("TENANT"))
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new JoinBuildingRequest(BUILDING_CODE))))
                        .andExpect(status().isCreated());

        when(currentUserService.getCurrentUser())
                        .thenReturn(currentManager());

        final UUID buildingId = buildingRepository.findByCode(BUILDING_CODE)
                        .orElseThrow()
                        .getId();

        mockMvc.perform(delete("/api/manager/buildings/{id}/tenants/{tenantUserId}",
                                        buildingId,
                                        TENANT_ID)
                                        .with(user("manager").roles("MANAGER"))
                                        .with(csrf()))
                        .andExpect(status().isNoContent());

        when(currentUserService.getCurrentUser())
                        .thenReturn(currentTenant());

        mockMvc.perform(get("/api/tenant/buildings/my-building")
                                        .with(user("tenant").roles("TENANT")))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.status").value(404));
    }

    private CurrentUser currentTenant() {
        return new CurrentUser(
                        TENANT_ID,
                        "ibrahim@example.com",
                        "TENANT",
                        "Brimoo",
                        "/images/avatar_me.jpg",
                        "+32000000000");
    }

    private CurrentUser currentManager() {
        return new CurrentUser(
                        MANAGER_ID,
                        "ibrahim.manager@example.com",
                        "MANAGER",
                        "Manager_Brimo",
                        "/images/avatar_manager.jpg",
                        "+32000000000");
    }
}