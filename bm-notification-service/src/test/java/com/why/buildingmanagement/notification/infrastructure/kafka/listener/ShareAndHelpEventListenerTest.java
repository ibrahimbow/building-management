package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
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
class ShareAndHelpEventListenerTest {

    @Mock
    private LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @Mock
    private CreateNotificationUseCase createNotificationUseCase;

    @InjectMocks
    private ShareAndHelpEventListener shareAndHelpEventListener;

    @Test
    void shouldCreatePostNotificationForAllTenantsExceptPostCreator() {
        final UUID buildingId = UUID.randomUUID();
        final Long creatorUserId = 10L;

        final ShareAndHelpPostCreatedEvent event = new ShareAndHelpPostCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        creatorUserId,
                        "Who can help with moving?",
                        "Ibrahim",
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L, 30L));

        shareAndHelpEventListener.onShareAndHelpPostCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase, times(2))
                        .createNotification(captor.capture());

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactly(20L, 30L);

        assertThat(captor.getAllValues())
                        .allSatisfy(command -> {
                            assertThat(command.buildingId()).isEqualTo(buildingId);
                            assertThat(command.type()).isEqualTo(NotificationType.SHARE_AND_HELP);
                            assertThat(command.title()).isEqualTo("New help & share post");
                            assertThat(command.message()).isEqualTo("Who can help with moving?");
                        });

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verifyNoMoreInteractions(loadBuildingTenantUsersPort, createNotificationUseCase);
    }

    @Test
    void shouldNotCreatePostNotificationWhenOnlyCreatorIsTenant() {
        final UUID buildingId = UUID.randomUUID();
        final Long creatorUserId = 10L;

        final ShareAndHelpPostCreatedEvent event = new ShareAndHelpPostCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        creatorUserId,
                        "Free table",
                        "Ibrahim",
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(creatorUserId));

        shareAndHelpEventListener.onShareAndHelpPostCreated(event);

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verifyNoInteractions(createNotificationUseCase);
        verifyNoMoreInteractions(loadBuildingTenantUsersPort);
    }

    @Test
    void shouldCreateCommentNotificationForPostOwner() {
        final UUID buildingId = UUID.randomUUID();
        final Long postOwnerUserId = 10L;
        final Long commentCreatedByUserId = 20L;

        final ShareAndHelpCommentCreatedEvent event = new ShareAndHelpCommentCreatedEvent(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        buildingId,
                        postOwnerUserId,
                        commentCreatedByUserId,
                        "Free table",
                        "Sarah",
                        Instant.now());

        shareAndHelpEventListener.onShareAndHelpCommentCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase).createNotification(captor.capture());

        final CreateNotificationCommand command = captor.getValue();

        assertThat(command.userId()).isEqualTo(postOwnerUserId);
        assertThat(command.buildingId()).isEqualTo(buildingId);
        assertThat(command.type()).isEqualTo(NotificationType.SHARE_AND_HELP);
        assertThat(command.title()).isEqualTo("New comment on your post");
        assertThat(command.message()).isEqualTo("Free table");

        verifyNoInteractions(loadBuildingTenantUsersPort);
        verifyNoMoreInteractions(createNotificationUseCase);
    }

    @Test
    void shouldNotCreateCommentNotificationWhenPostOwnerCommentsOnOwnPost() {
        final UUID buildingId = UUID.randomUUID();
        final Long postOwnerUserId = 10L;

        final ShareAndHelpCommentCreatedEvent event = new ShareAndHelpCommentCreatedEvent(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        buildingId,
                        postOwnerUserId,
                        postOwnerUserId,
                        "Free table",
                        "Ibrahim",
                        Instant.now());

        shareAndHelpEventListener.onShareAndHelpCommentCreated(event);

        verifyNoInteractions(createNotificationUseCase, loadBuildingTenantUsersPort);
    }
}