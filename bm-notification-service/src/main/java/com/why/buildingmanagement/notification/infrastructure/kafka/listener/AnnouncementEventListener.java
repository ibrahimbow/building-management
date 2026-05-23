package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.application.port.out.NotificationRepositoryPort;
import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementEventListener {

    private final NotificationRepositoryPort notificationRepositoryPort;

    @KafkaListener(
                    topics = KafkaTopics.ANNOUNCEMENT_CREATED_V1,
                    groupId = "bm-notification-service")
    public void handleAnnouncementCreated(final AnnouncementCreatedEvent event) {

        log.info("Received announcement created event: announcementId={}, buildingId={}, title={}",
                        event.announcementId(),
                        event.buildingId(),
                        event.title());

        final Notification notification = Notification.createNew(
                        1L,
                        event.buildingId(),
                        NotificationType.ANNOUNCEMENT,
                        event.title(),
                        event.title());

        notificationRepositoryPort.save(notification);

        log.info("Notification saved successfully for buildingId={}", event.buildingId());
    }
}