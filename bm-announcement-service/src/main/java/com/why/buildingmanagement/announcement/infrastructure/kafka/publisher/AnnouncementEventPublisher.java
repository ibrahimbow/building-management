package com.why.buildingmanagement.announcement.infrastructure.kafka.publisher;

import com.why.buildingmanagement.announcement.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.announcement.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAnnouncementCreated(
                    final AnnouncementCreatedEvent event) {

        log.info("Publishing announcement created event: {}", event.announcementId());

        kafkaTemplate.send(
                        KafkaTopics.ANNOUNCEMENT_CREATED_V1,
                        event.buildingId().toString(),
                        event);
    }
}