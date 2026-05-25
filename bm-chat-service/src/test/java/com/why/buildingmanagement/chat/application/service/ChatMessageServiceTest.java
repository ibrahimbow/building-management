package com.why.buildingmanagement.chat.application.service;

import com.why.buildingmanagement.chat.application.port.in.SendChatMessageCommand;
import com.why.buildingmanagement.chat.application.port.out.*;
import com.why.buildingmanagement.chat.domain.model.ChatMessage;
import com.why.buildingmanagement.chat.infrastructure.kafka.event.ChatMessageCreatedEvent;
import com.why.buildingmanagement.chat.infrastructure.kafka.publisher.ChatEventPublisher;
import com.why.buildingmanagement.chat.infrastructure.websocket.ChatWebSocketPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Long TENANT_USER_ID = 1001L;
    private static final Long MANAGER_USER_ID = 2001L;
    private static final UUID MESSAGE_ID = UUID.randomUUID();

    @Mock
    private SaveChatMessagePort saveChatMessagePort;

    @Mock
    private LoadChatMessagePort loadChatMessagePort;

    @Mock
    private LoadTenantBuildingPort loadTenantBuildingPort;

    @Mock
    private LoadManagerBuildingPort loadManagerBuildingPort;

    @Mock
    private SaveChatReactionPort saveChatReactionPort;

    @Mock
    private LoadChatReactionPort loadChatReactionPort;

    @Mock
    private DeleteChatReactionPort deleteChatReactionPort;

    @Mock
    private ChatWebSocketPublisher chatWebSocketPublisher;

    @Mock
    private ChatEventPublisher chatEventPublisher;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    void shouldSendMessageForTenantActiveBuilding() {

        final SendChatMessageCommand command = new SendChatMessageCommand(
                        TENANT_USER_ID,
                        "Tenant User",
                        "",
                        "Hello chat",
                        "");

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(TENANT_USER_ID))
                        .thenReturn(BUILDING_ID);

        when(saveChatMessagePort.save(any(ChatMessage.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        chatMessageService.send(command);

        verify(chatWebSocketPublisher).publishMessageCreated(any());

        final ArgumentCaptor<ChatMessageCreatedEvent> eventCaptor =
                        ArgumentCaptor.forClass(ChatMessageCreatedEvent.class);

        verify(chatEventPublisher).publishMessageCreated(eventCaptor.capture());

        final ChatMessageCreatedEvent event = eventCaptor.getValue();

        assertThat(event.buildingId()).isEqualTo(BUILDING_ID);
        assertThat(event.senderUserId()).isEqualTo(TENANT_USER_ID);
        assertThat(event.senderDisplayName()).isEqualTo("Tenant User");
        assertThat(event.content()).isEqualTo("Hello chat");
        assertThat(event.imageUrl()).isBlank();

        final ArgumentCaptor<ChatMessage> messageCaptor =
                        ArgumentCaptor.forClass(ChatMessage.class);

        verify(saveChatMessagePort).save(messageCaptor.capture());

        final ChatMessage savedMessage = messageCaptor.getValue();

        assertThat(savedMessage.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(savedMessage.getSenderUserId()).isEqualTo(TENANT_USER_ID);
        assertThat(savedMessage.getSenderDisplayName()).isEqualTo("Tenant User");
        assertThat(savedMessage.getSenderAvatarUrl()).isEqualTo("");
        assertThat(savedMessage.getContent()).isEqualTo("Hello chat");
        assertThat(savedMessage.getImageUrl()).isEqualTo("");
        assertThat(savedMessage.isDeleted()).isFalse();
    }

    @Test
    void shouldSendMessageForManagerOwnedBuilding() {

        final SendChatMessageCommand command = new SendChatMessageCommand(
                        MANAGER_USER_ID,
                        "Manager User",
                        "",
                        "Hello tenants",
                        "");

        when(loadManagerBuildingPort.loadBuildingIdByManagerUserId(MANAGER_USER_ID))
                        .thenReturn(BUILDING_ID);

        when(saveChatMessagePort.save(any(ChatMessage.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        chatMessageService.sendFromCurrentManagerBuilding(command);

        verify(chatWebSocketPublisher).publishMessageCreated(any());

        final ArgumentCaptor<ChatMessage> messageCaptor =
                        ArgumentCaptor.forClass(ChatMessage.class);

        verify(saveChatMessagePort).save(messageCaptor.capture());

        final ChatMessage savedMessage = messageCaptor.getValue();

        assertThat(savedMessage.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(savedMessage.getSenderUserId()).isEqualTo(MANAGER_USER_ID);
        assertThat(savedMessage.getSenderDisplayName()).isEqualTo("Manager User");
        assertThat(savedMessage.getContent()).isEqualTo("Hello tenants");
        assertThat(savedMessage.isDeleted()).isFalse();
    }

    @Test
    void shouldSoftDeleteOwnMessage() {

        final ChatMessage message = ChatMessage.createNew(
                        BUILDING_ID,
                        TENANT_USER_ID,
                        "Tenant User",
                        "",
                        "Hello chat",
                        "");

        when(loadChatMessagePort.findById(MESSAGE_ID))
                        .thenReturn(Optional.of(message));

        when(saveChatMessagePort.save(any(ChatMessage.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        chatMessageService.delete(MESSAGE_ID, TENANT_USER_ID);

        assertThat(message.isDeleted()).isTrue();
        assertThat(message.getDeletedAt()).isNotNull();

        verify(saveChatMessagePort).save(message);
        verify(chatWebSocketPublisher).publishMessageDeleted(any());
    }
}