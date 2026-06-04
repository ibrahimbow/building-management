package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingManagerUserPort;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShareAndHelpEventListener {

    private static final String SHARE_AND_HELP_POST_CREATED_TOPIC =
                    KafkaTopics.SHARE_AND_HELP_POST_CREATED_V1;

    private static final String SHARE_AND_HELP_COMMENT_CREATED_TOPIC =
                    KafkaTopics.SHARE_AND_HELP_COMMENT_CREATED_V1;

    private final LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;
    private final LoadBuildingManagerUserPort loadBuildingManagerUserPort;
    private final CreateNotificationUseCase createNotificationUseCase;

    @KafkaListener(
                    topics = SHARE_AND_HELP_POST_CREATED_TOPIC,
                    groupId = "bm-notification-service",
                    containerFactory = "shareAndHelpPostCreatedKafkaListenerContainerFactory")
    public void onShareAndHelpPostCreated(final ShareAndHelpPostCreatedEvent event) {

        log.info("Received share and help post created event: postId={}, buildingId={}, title={}",
                        event.postId(),
                        event.buildingId(),
                        event.title());

        final var recipientUserIds = new LinkedHashSet<Long>(
                        loadBuildingTenantUsersPort.loadTenantUserIds(event.buildingId()));

        final Long managerUserId =
                        loadBuildingManagerUserPort.loadManagerUserIdByBuildingId(event.buildingId());

        recipientUserIds.add(managerUserId);
        recipientUserIds.remove(event.createdByUserId());

        if (recipientUserIds.isEmpty()) {
            log.info("No share and help post recipients found for buildingId={}, createdByUserId={}",
                            event.buildingId(),
                            event.createdByUserId());
            return;
        }

        recipientUserIds.forEach(userId ->
                        createNotificationUseCase.createNotification(
                                        new CreateNotificationCommand(
                                                        userId,
                                                        event.buildingId(),
                                                        NotificationType.SHARE_AND_HELP,
                                                        "New help & share post",
                                                        event.title())));

        log.info("Created {} share and help post notifications for buildingId={}",
                        recipientUserIds.size(),
                        event.buildingId());
    }

    @KafkaListener(
                    topics = SHARE_AND_HELP_COMMENT_CREATED_TOPIC,
                    groupId = "bm-notification-service",
                    containerFactory = "shareAndHelpCommentCreatedKafkaListenerContainerFactory")
    public void onShareAndHelpCommentCreated(final ShareAndHelpCommentCreatedEvent event) {

        log.info("Received share and help comment created event: commentId={}, postId={}, buildingId={}",
                        event.commentId(),
                        event.postId(),
                        event.buildingId());

        if (event.postOwnerUserId().equals(event.commentCreatedByUserId())) {
            log.info("Skipping self comment notification for userId={}",
                            event.postOwnerUserId());
            return;
        }

        createNotificationUseCase.createNotification(
                        new CreateNotificationCommand(
                                        event.postOwnerUserId(),
                                        event.buildingId(),
                                        NotificationType.SHARE_AND_HELP,
                                        "New comment on your post",
                                        event.postTitle()));

        log.info("Created share and help comment notification for postOwnerUserId={}",
                        event.postOwnerUserId());
    }
}