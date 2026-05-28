package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementEventListenerTest {

    @Mock
    private CreateNotificationUseCase createNotificationUseCase;

    @Mock
    private LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @InjectMocks
    private AnnouncementEventListener announcementEventListener;

    @Test
    void shouldCreateNotificationForEachTenantWhenAnnouncementCreated() {
        final UUID announcementId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        final AnnouncementCreatedEvent event = new AnnouncementCreatedEvent(
                        announcementId,
                        buildingId,
                        "Water maintenance",
                        "MAINTENANCE",
                        "Manager One",
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L, 30L));

        announcementEventListener.handleAnnouncementCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase, times(3))
                        .createNotification(captor.capture());

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactly(10L, 20L, 30L);

        assertThat(captor.getAllValues())
                        .allSatisfy(command -> {
                            assertThat(command.buildingId()).isEqualTo(buildingId);
                            assertThat(command.type()).isEqualTo(NotificationType.ANNOUNCEMENT);
                            assertThat(command.title()).isEqualTo("Water maintenance");
                            assertThat(command.message()).isEqualTo("Water maintenance");
                        });

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verifyNoMoreInteractions(loadBuildingTenantUsersPort, createNotificationUseCase);
    }

    @Test
    void shouldNotCreateNotificationsWhenBuildingHasNoActiveTenants() {
        final UUID buildingId = UUID.randomUUID();

        final AnnouncementCreatedEvent event = new AnnouncementCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        "General update",
                        "GENERAL",
                        "Manager One",
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of());

        announcementEventListener.handleAnnouncementCreated(event);

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verifyNoInteractions(createNotificationUseCase);
        verifyNoMoreInteractions(loadBuildingTenantUsersPort);
    }
}