package com.why.buildingmanagement.notification.infrastructure.kafka.listener;

import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnnouncementEventListener {

    @KafkaListener(
                    topics = KafkaTopics.ANNOUNCEMENT_CREATED_V1,
                    groupId = "bm-notification-service")
    public void handleAnnouncementCreated(
                    final AnnouncementCreatedEvent event) {

        log.info(
                        "Received announcement created event: announcementId={}, buildingId={}, title={}",
                        event.announcementId(),
                        event.buildingId(),
                        event.title());
    }
}