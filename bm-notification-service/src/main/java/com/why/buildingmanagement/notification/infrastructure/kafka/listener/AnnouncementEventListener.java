package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementEventListener {
    private static final String ANNOUNCEMENT_CREATED_TOPIC = KafkaTopics.ANNOUNCEMENT_CREATED_V1;

    private final CreateNotificationUseCase createNotificationUseCase;
    private final LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @KafkaListener(
                    topics = ANNOUNCEMENT_CREATED_TOPIC,
                    groupId = "bm-notification-service",
                    containerFactory = "announcementCreatedKafkaListenerContainerFactory")
    public void handleAnnouncementCreated(final AnnouncementCreatedEvent event) {

        log.info("Received announcement created event: announcementId={}, buildingId={}, title={}",
                        event.announcementId(),
                        event.buildingId(),
                        event.title());

        final List<Long> tenantUserIds =
                        loadBuildingTenantUsersPort.loadTenantUserIds(event.buildingId());

        if (tenantUserIds.isEmpty()) {
            log.info("No active tenants found for buildingId={}",
                            event.buildingId());
            return;
        }

        tenantUserIds.forEach(userId ->
                        createNotificationUseCase.createNotification(
                                        new CreateNotificationCommand(
                                                        userId,
                                                        event.buildingId(),
                                                        NotificationType.ANNOUNCEMENT,
                                                        event.title(),
                                                        event.title())));

        log.info("Created {} announcement notifications for buildingId={}",
                        tenantUserIds.size(),
                        event.buildingId());
    }
}