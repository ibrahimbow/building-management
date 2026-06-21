package com.why.buildingmanagement.auth.infrastructure.kafka.publisher;

import com.why.buildingmanagement.auth.infrastructure.kafka.event.AuditEventMessage;
import com.why.buildingmanagement.auth.infrastructure.kafka.event.AuditEventType;
import com.why.buildingmanagement.auth.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAuditEventPublisher implements AuditEventPublisher {

    private final KafkaTemplate<String, AuditEventMessage> kafkaTemplate;

    @Override
    public void publish(final Long userId,
                        final String username,
                        final AuditEventType eventType,
                        final String description) {

        final AuditEventMessage message = new AuditEventMessage(userId,
                                                                username,
                                                                eventType,
                                                                description);

        kafkaTemplate.send(KafkaTopics.AUDIT_EVENTS, String.valueOf(userId), message)
                     .whenComplete((result, exception) -> {

                         if (exception != null) {
                             log.error("Failed to publish audit event. Type: {}, UserId: {}",
                                       eventType,
                                       userId,
                                       exception);

                             return;
                         }

                         log.info("Audit event published successfully. Type: {}, UserId: {}",
                                  eventType,
                                  userId);
                     });
    }
}