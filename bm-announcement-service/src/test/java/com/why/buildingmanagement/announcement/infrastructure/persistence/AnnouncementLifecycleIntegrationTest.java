package com.why.buildingmanagement.announcement.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.announcement.AnnouncementServiceApplication;
import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.CreateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.UpdateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.kafka.publisher.AnnouncementEventPublisher;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AnnouncementServiceApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class AnnouncementLifecycleIntegrationTest {

    @Container
    static final org.testcontainers.postgresql.PostgreSQLContainer postgres =
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

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private BuildingAccessPort buildingAccessPort;

    @MockitoBean
    private AnnouncementEventPublisher announcementEventPublisher;

    private UUID buildingId;

    @BeforeEach
    void setUp() {
        reset(currentUserService, buildingAccessPort, announcementEventPublisher);

        buildingId = UUID.randomUUID();

        when(currentUserService.getCurrentUser())
                .thenReturn(currentManager());

        when(buildingAccessPort.getManagerBuildingId(1L))
                .thenReturn(buildingId);

        when(buildingAccessPort.getTenantActiveBuildingId(2L))
                .thenReturn(buildingId);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void announcementLifecycle_shouldCreateViewUpdateDeleteAnnouncement() throws Exception {
        final CreateAnnouncementRequest createRequest = new CreateAnnouncementRequest(
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "https://example.com/water.jpg"
        );

        final String response = mockMvc.perform(post("/api/manager/announcements")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$.title").value("Water maintenance"))
                .andExpect(jsonPath("$.message").value("Water will be off tomorrow"))
                .andExpect(jsonPath("$.category").value("MAINTENANCE"))
                .andExpect(jsonPath("$.icon").value("build"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/water.jpg"))
                .andExpect(jsonPath("$.createdBy").value("Ibrahim"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final String announcementId = objectMapper
                .readTree(response)
                .get("id")
                .asText();

        mockMvc.perform(get("/api/manager/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(announcementId))
                .andExpect(jsonPath("$[0].buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$[0].title").value("Water maintenance"));

        final UpdateAnnouncementRequest updateRequest = new UpdateAnnouncementRequest(
                "Updated emergency notice",
                "Please keep the emergency exit clear",
                AnnouncementCategory.EMERGENCY,
                null
        );

        mockMvc.perform(put("/api/manager/announcements/{id}", announcementId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated emergency notice"))
                .andExpect(jsonPath("$.message").value("Please keep the emergency exit clear"))
                .andExpect(jsonPath("$.category").value("EMERGENCY"))
                .andExpect(jsonPath("$.icon").value("warning"))
                .andExpect(jsonPath("$.imageUrl").doesNotExist());

        when(currentUserService.getCurrentUser())
                .thenReturn(currentTenant());

        mockMvc.perform(get("/api/tenant/announcements")
                        .with(user("tenant").roles("TENANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(announcementId))
                .andExpect(jsonPath("$[0].title").value("Updated emergency notice"));

        when(currentUserService.getCurrentUser())
                .thenReturn(currentManager());

        mockMvc.perform(delete("/api/manager/announcements/{id}", announcementId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/manager/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private CurrentUser currentManager() {
        return new CurrentUser(
                1L,
                "Ibrahim",
                "ibrahim@example.com",
                "+32000000000",
                "MANAGER"
        );
    }

    private CurrentUser currentTenant() {
        return new CurrentUser(
                2L,
                "tenant",
                "tenant@example.com",
                "+32000000000",
                "TENANT"
        );
    }
}