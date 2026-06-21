package com.why.buildingmanagement.audit.application.result;

import com.why.buildingmanagement.audit.domain.model.AuditEventType;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResult(UUID id,
                               Long userId,
                               String username,
                               AuditEventType eventType,
                               String description,
                               Instant createdAt) {
}