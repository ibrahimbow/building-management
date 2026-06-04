package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingManagerUserPort;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ChatMessageCreatedEvent;
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
class ChatMessageEventListenerTest {

    @Mock
    private CreateNotificationUseCase createNotificationUseCase;

    @Mock
    private LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @Mock
    private LoadBuildingManagerUserPort loadBuildingManagerUserPort;

    @InjectMocks
    private ChatMessageEventListener chatMessageEventListener;

    @Test
    void shouldCreateChatNotificationForOtherTenantsAndManagerExceptMessageSender() {
        final UUID buildingId = UUID.randomUUID();
        final Long senderUserId = 10L;
        final Long managerUserId = 99L;

        final ChatMessageCreatedEvent event = new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        senderUserId,
                        "Ibrahim",
                        "Hello everyone",
                        null,
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L, 30L));

        when(loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(buildingId))
                        .thenReturn(managerUserId);

        chatMessageEventListener.handleChatMessageCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase, times(3))
                        .createNotification(captor.capture());

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactly(20L, 30L, managerUserId);

        assertThat(captor.getAllValues())
                        .allSatisfy(command -> {
                            assertThat(command.buildingId()).isEqualTo(buildingId);
                            assertThat(command.type()).isEqualTo(NotificationType.CHAT);
                            assertThat(command.title()).isEqualTo("New chat message");
                            assertThat(command.message()).isEqualTo("Ibrahim sent a new message");
                        });

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verify(loadBuildingManagerUserPort).loadManagerUserIdByBuildingId(buildingId);
        verifyNoMoreInteractions(
                        loadBuildingTenantUsersPort,
                        loadBuildingManagerUserPort,
                        createNotificationUseCase);
    }

    @Test
    void shouldCreateChatNotificationOnlyForManagerWhenSenderIsOnlyTenant() {
        final UUID buildingId = UUID.randomUUID();
        final Long senderUserId = 10L;
        final Long managerUserId = 99L;

        final ChatMessageCreatedEvent event = new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        senderUserId,
                        "Ibrahim",
                        "Hello everyone",
                        null,
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(senderUserId));

        when(loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(buildingId))
                        .thenReturn(managerUserId);

        chatMessageEventListener.handleChatMessageCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase)
                        .createNotification(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(managerUserId);
        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().type()).isEqualTo(NotificationType.CHAT);
        assertThat(captor.getValue().title()).isEqualTo("New chat message");
        assertThat(captor.getValue().message()).isEqualTo("Ibrahim sent a new message");

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verify(loadBuildingManagerUserPort).loadManagerUserIdByBuildingId(buildingId);
        verifyNoMoreInteractions(
                        loadBuildingTenantUsersPort,
                        loadBuildingManagerUserPort,
                        createNotificationUseCase);
    }

    @Test
    void shouldCreateChatNotificationForManagerWhenBuildingHasNoTenants() {
        final UUID buildingId = UUID.randomUUID();
        final Long managerUserId = 99L;

        final ChatMessageCreatedEvent event = new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        10L,
                        "Ibrahim",
                        "Hello everyone",
                        null,
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of());

        when(loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(buildingId))
                        .thenReturn(managerUserId);

        chatMessageEventListener.handleChatMessageCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase)
                        .createNotification(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(managerUserId);
        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().type()).isEqualTo(NotificationType.CHAT);
        assertThat(captor.getValue().title()).isEqualTo("New chat message");
        assertThat(captor.getValue().message()).isEqualTo("Ibrahim sent a new message");

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verify(loadBuildingManagerUserPort).loadManagerUserIdByBuildingId(buildingId);
        verifyNoMoreInteractions(
                        loadBuildingTenantUsersPort,
                        loadBuildingManagerUserPort,
                        createNotificationUseCase);
    }

    @Test
    void shouldNotCreateChatNotificationWhenSenderIsManagerAndBuildingHasNoOtherRecipients() {
        final UUID buildingId = UUID.randomUUID();
        final Long managerUserId = 99L;

        final ChatMessageCreatedEvent event = new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        managerUserId,
                        "Manager",
                        "Hello tenants",
                        null,
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of());

        when(loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(buildingId))
                        .thenReturn(managerUserId);

        chatMessageEventListener.handleChatMessageCreated(event);

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verify(loadBuildingManagerUserPort).loadManagerUserIdByBuildingId(buildingId);
        verifyNoInteractions(createNotificationUseCase);
        verifyNoMoreInteractions(loadBuildingTenantUsersPort, loadBuildingManagerUserPort);
    }

    @Test
    void shouldCreateChatNotificationForImageOnlyMessage() {
        final UUID buildingId = UUID.randomUUID();
        final Long senderUserId = 10L;
        final Long managerUserId = 99L;

        final ChatMessageCreatedEvent event = new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        senderUserId,
                        "Sarah",
                        null,
                        "/api/files/CHAT_MESSAGE_IMAGE/image.png",
                        Instant.now());

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L));

        when(loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(buildingId))
                        .thenReturn(managerUserId);

        chatMessageEventListener.handleChatMessageCreated(event);

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        verify(createNotificationUseCase, times(2))
                        .createNotification(captor.capture());

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactly(20L, managerUserId);

        assertThat(captor.getAllValues())
                        .allSatisfy(command -> {
                            assertThat(command.buildingId()).isEqualTo(buildingId);
                            assertThat(command.type()).isEqualTo(NotificationType.CHAT);
                            assertThat(command.title()).isEqualTo("New chat message");
                            assertThat(command.message()).isEqualTo("Sarah sent a new message");
                        });

        verify(loadBuildingTenantUsersPort).loadTenantUserIds(buildingId);
        verify(loadBuildingManagerUserPort).loadManagerUserIdByBuildingId(buildingId);
        verifyNoMoreInteractions(
                        loadBuildingTenantUsersPort,
                        loadBuildingManagerUserPort,
                        createNotificationUseCase);
    }
}