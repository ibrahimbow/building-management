package com.why.buildingmanagement.announcement.infrastructure.api.controller.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.announcement.application.port.in.CreateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.CreateAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.port.in.DeleteAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.DeleteAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.port.in.GetManagerAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.GetManagerAnnouncementsUseCase;
import com.why.buildingmanagement.announcement.application.port.in.UpdateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.UpdateAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.CreateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.UpdateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import com.why.buildingmanagement.announcement.infrastructure.api.mapper.AnnouncementApiMapper;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerAnnouncementController.class)
class ManagerAnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateAnnouncementUseCase createAnnouncementUseCase;

    @MockitoBean
    private UpdateAnnouncementUseCase updateAnnouncementUseCase;

    @MockitoBean
    private DeleteAnnouncementUseCase deleteAnnouncementUseCase;

    @MockitoBean
    private GetManagerAnnouncementsUseCase getManagerAnnouncementsUseCase;

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
                .thenReturn(currentManager());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createAnnouncement_shouldCreateAnnouncementForCurrentManager() throws Exception {
        final CreateAnnouncementRequest request = new CreateAnnouncementRequest(
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "https://example.com/image.jpg");

        final AnnouncementResult result = announcementResult();
        final AnnouncementResponse response = announcementResponse();

        when(createAnnouncementUseCase.createAnnouncement(any(CreateAnnouncementCommand.class)))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(post("/api/manager/announcements")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/manager/announcements/" + announcementId))
                .andExpect(jsonPath("$.id").value(announcementId.toString()))
                .andExpect(jsonPath("$.buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$.title").value("Water maintenance"))
                .andExpect(jsonPath("$.message").value("Water will be off tomorrow"))
                .andExpect(jsonPath("$.category").value("MAINTENANCE"))
                .andExpect(jsonPath("$.icon").value("build"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.createdBy").value("Ibrahim"));

        final ArgumentCaptor<CreateAnnouncementCommand> captor =
                ArgumentCaptor.forClass(CreateAnnouncementCommand.class);

        verify(createAnnouncementUseCase).createAnnouncement(captor.capture());

        assertThat(captor.getValue().managerId()).isEqualTo(1L);
        assertThat(captor.getValue().createdBy()).isEqualTo("Ibrahim");
        assertThat(captor.getValue().title()).isEqualTo("Water maintenance");
        assertThat(captor.getValue().message()).isEqualTo("Water will be off tomorrow");
        assertThat(captor.getValue().category()).isEqualTo(AnnouncementCategory.MAINTENANCE);
        assertThat(captor.getValue().imageUrl()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getManagerAnnouncements_shouldReturnCurrentManagersAnnouncements() throws Exception {
        final AnnouncementResult result = announcementResult();
        final AnnouncementResponse response = announcementResponse();

        when(getManagerAnnouncementsUseCase.getManagerAnnouncements(any(GetManagerAnnouncementsQuery.class)))
                .thenReturn(List.of(result));

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(get("/api/manager/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(announcementId.toString()))
                .andExpect(jsonPath("$[0].buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$[0].title").value("Water maintenance"))
                .andExpect(jsonPath("$[0].message").value("Water will be off tomorrow"))
                .andExpect(jsonPath("$[0].category").value("MAINTENANCE"))
                .andExpect(jsonPath("$[0].createdBy").value("Ibrahim"));

        final ArgumentCaptor<GetManagerAnnouncementsQuery> captor =
                ArgumentCaptor.forClass(GetManagerAnnouncementsQuery.class);

        verify(getManagerAnnouncementsUseCase).getManagerAnnouncements(captor.capture());

        assertThat(captor.getValue().managerId()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateAnnouncement_shouldUpdateAnnouncementForCurrentManager() throws Exception {
        final UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(
                "Updated title",
                "Updated message",
                AnnouncementCategory.EMERGENCY,
                null);

        final AnnouncementResult result = new AnnouncementResult(
                announcementId,
                buildingId,
                1L,
                "Ibrahim",
                "Updated title",
                "Updated message",
                AnnouncementCategory.EMERGENCY,
                "warning",
                null,
                Instant.parse("2026-05-09T10:00:00Z"),
                Instant.parse("2026-05-09T11:00:00Z"));

        final AnnouncementResponse response = new AnnouncementResponse(
                announcementId.toString(),
                buildingId.toString(),
                "Updated title",
                "Updated message",
                AnnouncementCategory.EMERGENCY,
                "warning",
                null,
                "Ibrahim",
                Instant.parse("2026-05-09T10:00:00Z"),
                Instant.parse("2026-05-09T11:00:00Z"));

        when(updateAnnouncementUseCase.updateAnnouncement(any(UpdateAnnouncementCommand.class)))
                .thenReturn(result);

        when(mapper.toResponse(result))
                .thenReturn(response);

        mockMvc.perform(put("/api/manager/announcements/{id}", announcementId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(announcementId.toString()))
                .andExpect(jsonPath("$.buildingId").value(buildingId.toString()))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.message").value("Updated message"))
                .andExpect(jsonPath("$.category").value("EMERGENCY"))
                .andExpect(jsonPath("$.icon").value("warning"))
                .andExpect(jsonPath("$.imageUrl").doesNotExist());

        final ArgumentCaptor<UpdateAnnouncementCommand> captor =
                ArgumentCaptor.forClass(UpdateAnnouncementCommand.class);

        verify(updateAnnouncementUseCase).updateAnnouncement(captor.capture());

        assertThat(captor.getValue().announcementId()).isEqualTo(announcementId);
        assertThat(captor.getValue().managerId()).isEqualTo(1L);
        assertThat(captor.getValue().title()).isEqualTo("Updated title");
        assertThat(captor.getValue().message()).isEqualTo("Updated message");
        assertThat(captor.getValue().category()).isEqualTo(AnnouncementCategory.EMERGENCY);
        assertThat(captor.getValue().imageUrl()).isNull();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteAnnouncement_shouldDeleteAnnouncementForCurrentManager() throws Exception {
        mockMvc.perform(delete("/api/manager/announcements/{id}", announcementId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        final ArgumentCaptor<DeleteAnnouncementCommand> captor =
                ArgumentCaptor.forClass(DeleteAnnouncementCommand.class);

        verify(deleteAnnouncementUseCase).deleteAnnouncement(captor.capture());

        assertThat(captor.getValue().announcementId()).isEqualTo(announcementId);
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