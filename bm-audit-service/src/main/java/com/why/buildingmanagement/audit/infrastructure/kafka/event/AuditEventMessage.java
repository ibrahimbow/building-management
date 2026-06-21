package com.why.buildingmanagement.audit.infrastructure.kafka.event;

import com.why.buildingmanagement.audit.domain.model.AuditEventType;

public record AuditEventMessage(Long userId,
                                String username,
                                AuditEventType eventType,
                                String description) {
}