package com.why.buildingmanagement.auth.infrastructure.kafka.publisher;

import com.why.buildingmanagement.auth.infrastructure.kafka.event.AuditEventType;

public interface AuditEventPublisher {

    void publish(final Long userId,
                 final String username,
                 final AuditEventType eventType,
                 final String description);
}