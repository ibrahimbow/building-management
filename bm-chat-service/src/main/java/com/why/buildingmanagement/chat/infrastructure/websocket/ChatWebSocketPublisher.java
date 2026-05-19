package com.why.buildingmanagement.chat.infrastructure.websocket;

import com.why.buildingmanagement.chat.application.result.ChatMessageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishMessageCreated(final ChatMessageResult message) {
        publishEvent(ChatWebSocketEventType.MESSAGE_CREATED, message);
    }

    public void publishMessageDeleted(final ChatMessageResult message) {
        publishEvent(ChatWebSocketEventType.MESSAGE_DELETED, message);
    }

    public void publishReactionUpdated(final ChatMessageResult message) {

        publishEvent(
                ChatWebSocketEventType.REACTION_UPDATED,
                message);
    }

    private void publishEvent(final ChatWebSocketEventType type,
                              final ChatMessageResult message) {

        final ChatWebSocketEvent event = new ChatWebSocketEvent(
                type,
                message.buildingId(),
                message);

        messagingTemplate.convertAndSend(
                "/topic/buildings/" + message.buildingId() + "/chat/messages",
                event);
    }
}