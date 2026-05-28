package com.why.buildingmanagement.notification.application.service;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.GetMyNotificationsCommand;
import com.why.buildingmanagement.notification.application.port.in.GetUnreadNotificationCountCommand;
import com.why.buildingmanagement.notification.application.port.in.MarkNotificationAsReadCommand;
import com.why.buildingmanagement.notification.application.port.out.NotificationRepositoryPort;
import com.why.buildingmanagement.notification.application.result.NotificationResult;
import com.why.buildingmanagement.notification.domain.exception.NotificationNotFoundException;
import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.websocket.publisher.NotificationWebSocketPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NotificationApplicationServiceTest {

    private NotificationRepositoryPort notificationRepositoryPort;
    private NotificationWebSocketPublisher notificationWebSocketPublisher;
    private NotificationApplicationService notificationApplicationService;

    @BeforeEach
    void setUp() {
        notificationRepositoryPort = mock(NotificationRepositoryPort.class);
        notificationWebSocketPublisher = mock(NotificationWebSocketPublisher.class);

        notificationApplicationService = new NotificationApplicationService(
                        notificationRepositoryPort,
                        notificationWebSocketPublisher);
    }

    @Test
    void shouldCreateNotification() {
        final Long userId = 10L;
        final UUID buildingId = UUID.randomUUID();

        final CreateNotificationCommand command = new CreateNotificationCommand(
                        userId,
                        buildingId,
                        NotificationType.ANNOUNCEMENT,
                        "New announcement",
                        "A new announcement was created");

        when(notificationRepositoryPort.save(any(Notification.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        final NotificationResult result =
                        notificationApplicationService.createNotification(command);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.buildingId()).isEqualTo(buildingId);
        assertThat(result.type()).isEqualTo(NotificationType.ANNOUNCEMENT);
        assertThat(result.title()).isEqualTo("New announcement");
        assertThat(result.message()).isEqualTo("A new announcement was created");
        assertThat(result.read()).isFalse();

        verify(notificationRepositoryPort).save(any(Notification.class));
        verify(notificationWebSocketPublisher).publishNotification(result);
        verify(notificationWebSocketPublisher).publishAnnouncementToBuilding(result);
    }

    @Test
    void shouldGetMyNotifications() {
        final Long userId = 10L;
        final UUID buildingId = UUID.randomUUID();

        final Notification notification = Notification.restore(
                        UUID.randomUUID(),
                        userId,
                        buildingId,
                        NotificationType.ANNOUNCEMENT,
                        "New announcement",
                        "A new announcement was created",
                        false,
                        Instant.now(),
                        null);

        when(notificationRepositoryPort.findByUserIdOrderByCreatedAtDesc(userId))
                        .thenReturn(List.of(notification));

        final List<NotificationResult> result =
                        notificationApplicationService.getMyNotifications(
                                        new GetMyNotificationsCommand(userId));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().userId()).isEqualTo(userId);
        assertThat(result.getFirst().title()).isEqualTo("New announcement");
        assertThat(result.getFirst().read()).isFalse();

        verify(notificationRepositoryPort).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldGetUnreadNotificationCount() {
        final Long userId = 10L;

        when(notificationRepositoryPort.countByUserIdAndReadFalse(userId))
                        .thenReturn(3L);

        final long result =
                        notificationApplicationService.getUnreadNotificationCount(
                                        new GetUnreadNotificationCountCommand(userId));

        assertThat(result).isEqualTo(3L);

        verify(notificationRepositoryPort).countByUserIdAndReadFalse(userId);
    }

    @Test
    void shouldMarkNotificationAsRead() {
        final UUID notificationId = UUID.randomUUID();
        final Long userId = 10L;
        final UUID buildingId = UUID.randomUUID();

        final Notification notification = Notification.restore(
                        notificationId,
                        userId,
                        buildingId,
                        NotificationType.SHARE_AND_HELP,
                        "New comment",
                        "Someone commented on your post",
                        false,
                        Instant.now(),
                        null);

        when(notificationRepositoryPort.findById(notificationId))
                        .thenReturn(Optional.of(notification));
        when(notificationRepositoryPort.save(any(Notification.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        final NotificationResult result =
                        notificationApplicationService.markNotificationAsRead(
                                        new MarkNotificationAsReadCommand(notificationId, userId));

        assertThat(result.id()).isEqualTo(notificationId);
        assertThat(result.read()).isTrue();
        assertThat(result.readAt()).isNotNull();

        verify(notificationRepositoryPort).findById(notificationId);
        verify(notificationRepositoryPort).save(notification);
        verify(notificationWebSocketPublisher).publishNotification(result);
    }

    @Test
    void shouldThrowExceptionWhenNotificationNotFound() {
        final UUID notificationId = UUID.randomUUID();
        final Long userId = 10L;

        when(notificationRepositoryPort.findById(notificationId))
                        .thenReturn(Optional.empty());

        assertThrows(
                        NotificationNotFoundException.class,
                        () -> notificationApplicationService.markNotificationAsRead(
                                        new MarkNotificationAsReadCommand(notificationId, userId)));

        verify(notificationRepositoryPort).findById(notificationId);
        verify(notificationRepositoryPort, never()).save(any());
        verifyNoInteractions(notificationWebSocketPublisher);
    }
}