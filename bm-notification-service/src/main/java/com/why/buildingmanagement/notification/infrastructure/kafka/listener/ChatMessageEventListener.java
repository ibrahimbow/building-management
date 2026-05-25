package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ChatMessageCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageEventListener {

    private static final String CHAT_MESSAGE_CREATED_TOPIC =
                    KafkaTopics.CHAT_MESSAGE_CREATED_V1;

    private final CreateNotificationUseCase createNotificationUseCase;
    private final LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @KafkaListener(
                    topics = CHAT_MESSAGE_CREATED_TOPIC,
                    groupId = "bm-notification-service",
                    containerFactory = "chatMessageCreatedKafkaListenerContainerFactory")
    public void handleChatMessageCreated(final ChatMessageCreatedEvent event) {

        log.info("Received chat message created event: messageId={}, buildingId={}, senderUserId={}",
                        event.messageId(),
                        event.buildingId(),
                        event.senderUserId());

        final List<Long> tenantUserIds =
                        loadBuildingTenantUsersPort.loadTenantUserIds(
                                        event.buildingId());

        final List<Long> recipientUserIds = tenantUserIds.stream()
                                        .filter(userId -> !userId.equals(event.senderUserId()))
                                        .toList();

        if (recipientUserIds.isEmpty()) {

            log.info("No recipients found for buildingId={}, senderUserId={}",
                            event.buildingId(),
                            event.senderUserId());

            return;
        }

        recipientUserIds.forEach(userId ->
                        createNotificationUseCase.createNotification(
                                        new CreateNotificationCommand(
                                                        userId,
                                                        event.buildingId(),
                                                        NotificationType.CHAT,
                                                        "New chat message",
                                                        event.senderDisplayName() + " sent a new message")));

        log.info("Created {} chat notifications for buildingId={}",
                        recipientUserIds.size(),
                        event.buildingId());
    }
}