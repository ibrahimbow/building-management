package com.why.buildingmanagement.auth.infrastructure.kafka.event;

public record AuditEventMessage(Long userId,
                                String username,
                                AuditEventType eventType,
                                String description) {
}