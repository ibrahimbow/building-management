package com.why.buildingmanagement.announcement.infrastructure.api.controller.tenant;

import com.why.buildingmanagement.announcement.application.port.in.GetTenantAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.GetTenantAnnouncementsUseCase;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import com.why.buildingmanagement.announcement.infrastructure.api.mapper.AnnouncementApiMapper;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantAnnouncementController.class)
class TenantAnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTenantAnnouncementsUseCase getTenantAnnouncementsUseCase;

    @MockitoBean
    private AnnouncementApiMapper mapper;

    @MockitoBean
    private CurrentUserService currentUserService;

    private UUID announcementId;
    private UUID buildingId;

    @BeforeEach
    void setUp() {
        announcementId = UUID.randomUUID();
        buildingId = UUID.randomUUID();

        when(currentUserService.getCurrentUser())
                .thenReturn(currentTenant());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void getTenantAnnouncements_shouldReturnAnnouncementsForCurrentTenantsBuilding() throws Exception {
        final AnnouncementResult result = announcementResult();
        final AnnouncementResponse response = announcementResponse();

        when(getTenantAnnouncementsUseCase.getTenantAnnouncements(any(GetTenantAnnouncementsQuery.class)))
                .thenReturn(List.of(result));

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/tenant/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(announcementId.toString()))
                .andExpect(jsonPath("$[0].buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$[0].title").value("Water maintenance"))
                .andExpect(jsonPath("$[0].message").value("Water will be off tomorrow"))
                .andExpect(jsonPath("$[0].category").value("MAINTENANCE"))
                .andExpect(jsonPath("$[0].icon").value("build"))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$[0].createdBy").value("Ibrahim"));

        final ArgumentCaptor<GetTenantAnnouncementsQuery> captor =
                ArgumentCaptor.forClass(GetTenantAnnouncementsQuery.class);

        verify(getTenantAnnouncementsUseCase).getTenantAnnouncements(captor.capture());

        assertThat(captor.getValue().tenantUserId()).isEqualTo(2L);
    }

    private CurrentUser currentTenant() {
        return new CurrentUser(
                2L,
                "tenant",
                "tenant@example.com",
                "+32000000000",
                "TENANT");
    }

    private AnnouncementResult announcementResult() {
        return new AnnouncementResult(
                announcementId,
                buildingId,
                1L,
                "Ibrahim",
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "build",
                "https://example.com/image.jpg",
                Instant.parse("2026-05-09T10:00:00Z"),
                null);
    }

    private AnnouncementResponse announcementResponse() {
        return new AnnouncementResponse(
                announcementId.toString(),
                buildingId.toString(),
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "build",
                "https://example.com/image.jpg",
                "Ibrahim",
                Instant.parse("2026-05-09T10:00:00Z"),
                null);
    }
}