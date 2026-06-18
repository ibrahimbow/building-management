package com.why.buildingmanagement.announcement.application.service;

import com.why.buildingmanagement.announcement.application.port.in.*;
import com.why.buildingmanagement.announcement.application.port.out.AnnouncementRepositoryPort;
import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementNotFoundException;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementOwnershipException;
import com.why.buildingmanagement.announcement.domain.model.Announcement;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import com.why.buildingmanagement.announcement.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.announcement.infrastructure.kafka.publisher.AnnouncementEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory.EMERGENCY;
import static com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory.MAINTENANCE;
import static com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory.SAFETY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementApplicationServiceTest {

    private static final Long MANAGER_ID = 1L;
    private static final Long TENANT_ID = 2L;
    private static final String CREATED_BY = "Ibrahim";

    @Mock
    private AnnouncementRepositoryPort announcementRepositoryPort;

    @Mock
    private BuildingAccessPort buildingAccessPort;

    @Mock
    private AnnouncementEventPublisher announcementEventPublisher;

    @InjectMocks
    private AnnouncementApplicationService announcementApplicationService;

    private UUID buildingId;
    private UUID announcementId;

    @BeforeEach
    void setUp() {
        buildingId = UUID.randomUUID();
        announcementId = UUID.randomUUID();
    }

    @Test
    void createAnnouncement_shouldCreateSavePublishAndReturnResult() {
        final CreateAnnouncementCommand command = new CreateAnnouncementCommand(MANAGER_ID,
                                                                                CREATED_BY,
                                                                                "Water maintenance",
                                                                                "Water will be off tomorrow",
                                                                                MAINTENANCE,
                                                                                "https://example.com/image.jpg");

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                        .thenReturn(buildingId);

        when(announcementRepositoryPort.save(any(Announcement.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        final AnnouncementResult result = announcementApplicationService.createAnnouncement(command);

        assertThat(result.buildingId()).isEqualTo(buildingId);
        assertThat(result.createdByManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.createdBy()).isEqualTo(CREATED_BY);
        assertThat(result.title()).isEqualTo("Water maintenance");
        assertThat(result.message()).isEqualTo("Water will be off tomorrow");
        assertThat(result.category()).isEqualTo(MAINTENANCE);
        assertThat(result.icon()).isEqualTo("build");
        assertThat(result.imageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.createdAt()).isNotNull();

        final ArgumentCaptor<Announcement> announcementCaptor = ArgumentCaptor.forClass(Announcement.class);

        verify(announcementRepositoryPort).save(announcementCaptor.capture());

        final Announcement savedAnnouncement = announcementCaptor.getValue();

        assertThat(savedAnnouncement.getBuildingId()).isEqualTo(buildingId);
        assertThat(savedAnnouncement.getCreatedByManagerId()).isEqualTo(MANAGER_ID);
        assertThat(savedAnnouncement.getCreatedBy()).isEqualTo(CREATED_BY);

        final ArgumentCaptor<AnnouncementCreatedEvent> eventCaptor =
                        ArgumentCaptor.forClass(AnnouncementCreatedEvent.class);

        verify(announcementEventPublisher).publishAnnouncementCreated(eventCaptor.capture());

        final AnnouncementCreatedEvent event = eventCaptor.getValue();

        assertThat(event.announcementId()).isEqualTo(savedAnnouncement.getId());
        assertThat(event.buildingId()).isEqualTo(buildingId);
        assertThat(event.title()).isEqualTo("Water maintenance");
        assertThat(event.category()).isEqualTo(MAINTENANCE.name());
        assertThat(event.createdByDisplayName()).isEqualTo(CREATED_BY);
        assertThat(event.createdAt()).isEqualTo(savedAnnouncement.getCreatedAt());

        verify(buildingAccessPort).getManagerBuildingId(MANAGER_ID);
        verifyNoMoreInteractions(buildingAccessPort, announcementRepositoryPort, announcementEventPublisher);
    }

    @Test
    void updateAnnouncement_shouldUpdateAndSave_whenManagerOwnsAnnouncement() {
        final Announcement existingAnnouncement = announcement();

        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(announcementId,
                                                                                MANAGER_ID,
                                                                                "Updated title",
                                                                                "Updated message",
                                                                                EMERGENCY,
                                                                                null);

        when(announcementRepositoryPort.findById(announcementId))
                        .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                        .thenReturn(buildingId);

        when(announcementRepositoryPort.save(existingAnnouncement))
                        .thenReturn(existingAnnouncement);

        final AnnouncementResult result =
                        announcementApplicationService.updateAnnouncement(command);

        assertThat(result.title()).isEqualTo("Updated title");
        assertThat(result.message()).isEqualTo("Updated message");
        assertThat(result.category()).isEqualTo(EMERGENCY);
        assertThat(result.icon()).isEqualTo("warning");
        assertThat(result.updatedAt()).isNotNull();

        verify(announcementRepositoryPort).findById(announcementId);
        verify(buildingAccessPort).getManagerBuildingId(MANAGER_ID);
        verify(announcementRepositoryPort).save(existingAnnouncement);
        verifyNoInteractions(announcementEventPublisher);
    }

    @Test
    void updateAnnouncement_shouldThrowNotFound_whenAnnouncementDoesNotExist() {
        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(announcementId,
                                                                                MANAGER_ID,
                                                                                "Updated title",
                                                                                "Updated message",
                                                                                SAFETY,
                                                                                null);

        when(announcementRepositoryPort.findById(announcementId))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> announcementApplicationService.updateAnnouncement(command))
                        .isInstanceOf(AnnouncementNotFoundException.class);

        verify(announcementRepositoryPort).findById(announcementId);
        verify(announcementRepositoryPort, never()).save(any());
        verifyNoInteractions(buildingAccessPort, announcementEventPublisher);
    }

    @Test
    void updateAnnouncement_shouldThrowOwnershipException_whenManagerDoesNotOwnBuilding() {
        final Announcement existingAnnouncement = announcement();

        final UpdateAnnouncementCommand command = new UpdateAnnouncementCommand(announcementId,
                                                                                MANAGER_ID,
                                                                                "Updated title",
                                                                                "Updated message",
                                                                                SAFETY,
                                                                                null);

        when(announcementRepositoryPort.findById(announcementId))
                        .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                        .thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> announcementApplicationService.updateAnnouncement(command))
                        .isInstanceOf(AnnouncementOwnershipException.class);

        verify(announcementRepositoryPort).findById(announcementId);
        verify(buildingAccessPort).getManagerBuildingId(MANAGER_ID);
        verify(announcementRepositoryPort, never()).save(any());
        verifyNoInteractions(announcementEventPublisher);
    }

    @Test
    void deleteAnnouncement_shouldDelete_whenManagerOwnsAnnouncement() {
        final Announcement existingAnnouncement = announcement();

        final DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(announcementId, MANAGER_ID);

        when(announcementRepositoryPort.findById(announcementId))
                        .thenReturn(Optional.of(existingAnnouncement));

        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                        .thenReturn(buildingId);

        announcementApplicationService.deleteAnnouncement(command);

        verify(announcementRepositoryPort).findById(announcementId);
        verify(buildingAccessPort).getManagerBuildingId(MANAGER_ID);
        verify(announcementRepositoryPort).delete(existingAnnouncement);
        verifyNoInteractions(announcementEventPublisher);
    }

    @Test
    void deleteAnnouncement_shouldThrowNotFound_whenAnnouncementDoesNotExist() {
        final DeleteAnnouncementCommand command =
                        new DeleteAnnouncementCommand(announcementId, MANAGER_ID);

        when(announcementRepositoryPort.findById(announcementId))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> announcementApplicationService.deleteAnnouncement(command))
                        .isInstanceOf(AnnouncementNotFoundException.class);

        verify(announcementRepositoryPort).findById(announcementId);
        verify(announcementRepositoryPort, never()).delete(any());
        verifyNoInteractions(buildingAccessPort, announcementEventPublisher);
    }

    @Test
    void getManagerAnnouncements_shouldReturnAnnouncementsForManagerBuilding() {
        when(buildingAccessPort.getManagerBuildingId(MANAGER_ID))
                        .thenReturn(buildingId);

        when(announcementRepositoryPort.findByBuildingId(buildingId))
                        .thenReturn(List.of(announcement()));

        final List<AnnouncementResult> results =
                        announcementApplicationService.getManagerAnnouncements(
                                        new GetManagerAnnouncementsQuery(MANAGER_ID));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().buildingId()).isEqualTo(buildingId);
        assertThat(results.getFirst().createdByManagerId()).isEqualTo(MANAGER_ID);

        verify(buildingAccessPort).getManagerBuildingId(MANAGER_ID);
        verify(announcementRepositoryPort).findByBuildingId(buildingId);
        verifyNoInteractions(announcementEventPublisher);
    }

    @Test
    void getTenantAnnouncements_shouldReturnAnnouncementsForTenantActiveBuilding() {
        when(buildingAccessPort.getTenantActiveBuildingId(TENANT_ID))
                        .thenReturn(buildingId);

        when(announcementRepositoryPort.findByBuildingId(buildingId))
                        .thenReturn(List.of(announcement()));

        final List<AnnouncementResult> results = announcementApplicationService.getTenantAnnouncements(
                        new GetTenantAnnouncementsQuery(TENANT_ID));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().buildingId()).isEqualTo(buildingId);

        verify(buildingAccessPort).getTenantActiveBuildingId(TENANT_ID);
        verify(announcementRepositoryPort).findByBuildingId(buildingId);
        verifyNoInteractions(announcementEventPublisher);
    }

    private Announcement announcement() {
        return Announcement.restore(announcementId,
                                    buildingId,
                                    MANAGER_ID,
                                    CREATED_BY,
                                    "Water maintenance",
                                    "Water will be off tomorrow",
                                    MAINTENANCE,
                                    "build",
                                    "https://example.com/image.jpg",
                                    Instant.now(),
                                    null);
    }
}