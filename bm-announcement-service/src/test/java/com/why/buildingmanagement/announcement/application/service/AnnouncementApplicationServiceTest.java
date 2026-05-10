package com.why.buildingmanagement.announcement.application.service;

import com.why.buildingmanagement.announcement.application.port.in.CreateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.DeleteAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.GetManagerAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.GetTenantAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.UpdateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.out.AnnouncementRepositoryPort;
import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementNotFoundException;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementOwnershipException;
import com.why.buildingmanagement.announcement.domain.model.Announcement;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AnnouncementApplicationServiceTest {

    private AnnouncementRepositoryPort announcementRepositoryPort;
    private BuildingAccessPort buildingAccessPort;
    private AnnouncementApplicationService announcementApplicationService;

    private static final Long MANAGER_ID = 1L;
    private static final Long TENANT_ID = 2L;

    private UUID buildingId;
    private UUID announcementId;

    @BeforeEach
    void setUp() {
        announcementRepositoryPort = mock(AnnouncementRepositoryPort.class);
        buildingAccessPort = mock(BuildingAccessPort.class);

        announcementApplicationService = new AnnouncementApplicationService(
                announcementRepositoryPort,
                buildingAccessPort);

        buildingId = UUID.randomUUID();
        announcementId = UUID.randomUUID();
    }

    @Test
    void createAnnouncement_shouldResolveManagerBuildingAndSaveAnnouncement() {
        final CreateAnnouncementCommand command = new CreateAnnouncementCommand(
                MANAGER_ID,
                "Ibrahim",
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "https://example.com/image.jpg");

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                .thenReturn(buildingId);

        when(announcementRepositoryPort.save(any(Announcement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final AnnouncementResult result =
                announcementApplicationService.createAnnouncement(command);

        assertThat(result.buildingId()).isEqualTo(buildingId);
        assertThat(result.createdByManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.createdBy()).isEqualTo("Ibrahim");
        assertThat(result.title()).isEqualTo("Water maintenance");
        assertThat(result.message()).isEqualTo("Water will be off tomorrow");
        assertThat(result.category()).isEqualTo("Maintenance");
        assertThat(result.icon()).isEqualTo("build");
        assertThat(result.imageUrl()).isEqualTo("https://example.com/image.jpg");

        final ArgumentCaptor<Announcement> captor =
                ArgumentCaptor.forClass(Announcement.class);

        verify(announcementRepositoryPort).save(captor.capture());

        final Announcement savedAnnouncement = captor.getValue();

        assertThat(savedAnnouncement.getBuildingId()).isEqualTo(buildingId);
        assertThat(savedAnnouncement.getCreatedByManagerId()).isEqualTo(MANAGER_ID);
        assertThat(savedAnnouncement.getCreatedBy()).isEqualTo("Ibrahim");
    }

    @Test
    void updateAnnouncement_shouldUpdateAnnouncement_whenManagerOwnsBuildingAndAnnouncement() {
        final Announcement existingAnnouncement = announcement();

        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(
                announcementId,
                MANAGER_ID,
                "Updated title",
                "Updated message",
                AnnouncementCategory.EMERGENCY,
                null);

        when(announcementRepositoryPort.findById(announcementId))
                .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                .thenReturn(buildingId);

        when(announcementRepositoryPort.save(any(Announcement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final AnnouncementResult result =
                announcementApplicationService.updateAnnouncement(command);

        assertThat(result.title()).isEqualTo("Updated title");
        assertThat(result.message()).isEqualTo("Updated message");
        assertThat(result.category()).isEqualTo("Safety");
        assertThat(result.icon()).isEqualTo("shield");
        assertThat(result.updatedAt()).isNotNull();

        verify(announcementRepositoryPort).save(existingAnnouncement);
    }

    @Test
    void updateAnnouncement_shouldThrowException_whenAnnouncementDoesNotExist() {
        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(
                announcementId,
                MANAGER_ID,
                "Updated title",
                "Updated message",
                AnnouncementCategory.SAFETY,
                null);

        when(announcementRepositoryPort.findById(announcementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> announcementApplicationService.updateAnnouncement(command))
                .isInstanceOf(AnnouncementNotFoundException.class);

        verify(announcementRepositoryPort, never()).save(any());
    }

    @Test
    void updateAnnouncement_shouldThrowException_whenManagerDoesNotOwnAnnouncementBuilding() {
        final Announcement existingAnnouncement = announcement();

        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(
                announcementId,
                MANAGER_ID,
                "Updated title",
                "Updated message",
                AnnouncementCategory.SAFETY,
                null);

        when(announcementRepositoryPort.findById(announcementId))
                .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                .thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> announcementApplicationService.updateAnnouncement(command))
                .isInstanceOf(AnnouncementOwnershipException.class);

        verify(announcementRepositoryPort, never()).save(any());
    }

    @Test
    void deleteAnnouncement_shouldDeleteAnnouncement_whenManagerOwnsIt() {
        final Announcement existingAnnouncement = announcement();

        final DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
                announcementId,
                MANAGER_ID);

        when(announcementRepositoryPort.findById(announcementId))
                .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                .thenReturn(buildingId);

        announcementApplicationService.deleteAnnouncement(command);

        verify(announcementRepositoryPort).delete(existingAnnouncement);
    }

    @Test
    void deleteAnnouncement_shouldThrowException_whenAnnouncementDoesNotExist() {
        final DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
                announcementId,
                MANAGER_ID);

        when(announcementRepositoryPort.findById(announcementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> announcementApplicationService.deleteAnnouncement(command))
                .isInstanceOf(AnnouncementNotFoundException.class);

        verify(announcementRepositoryPort, never()).delete(any());
    }

    @Test
    void getManagerAnnouncements_shouldReturnAnnouncementsForManagerBuilding() {
        final Announcement announcement = announcement();

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                .thenReturn(buildingId);

        when(announcementRepositoryPort.findByBuildingId(buildingId))
                .thenReturn(List.of(announcement));

        final List<AnnouncementResult> results =
                announcementApplicationService.getManagerAnnouncements(
                        new GetManagerAnnouncementsQuery(MANAGER_ID));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().buildingId()).isEqualTo(buildingId);
        assertThat(results.getFirst().createdByManagerId()).isEqualTo(MANAGER_ID);
    }

    @Test
    void getTenantAnnouncements_shouldReturnAnnouncementsForTenantActiveBuilding() {
        final Announcement announcement = announcement();

        when(buildingAccessPort.getTenantActiveBuildingId(TENANT_ID))
                .thenReturn(buildingId);

        when(announcementRepositoryPort.findByBuildingId(buildingId))
                .thenReturn(List.of(announcement));

        final List<AnnouncementResult> results =
                announcementApplicationService.getTenantAnnouncements(
                        new GetTenantAnnouncementsQuery(TENANT_ID));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().buildingId()).isEqualTo(buildingId);
    }

    private Announcement announcement() {
        return Announcement.restore(
                announcementId,
                buildingId,
                MANAGER_ID,
                "Ibrahim",
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "build",
                "https://example.com/image.jpg",
                java.time.Instant.now(),
                null);
    }
}