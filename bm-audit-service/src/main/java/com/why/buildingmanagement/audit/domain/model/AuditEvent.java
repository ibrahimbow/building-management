package com.why.buildingmanagement.audit.domain.model;

import com.why.buildingmanagement.audit.domain.exception.InvalidAuditEventException;
import com.why.buildingmanagement.audit.domain.validation.AuditEventValidator;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class AuditEvent {

    private final UUID id;
    private final Long userId;
    private final String username;
    private final AuditEventType eventType;
    private final String description;
    private final Instant createdAt;

    private AuditEvent(final UUID id,
                       final Long userId,
                       final String username,
                       final AuditEventType eventType,
                       final String description,
                       final Instant createdAt) {

        AuditEventValidator.validate(eventType, description, createdAt);

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.eventType = eventType;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static AuditEvent createNew(final Long userId,
                                       final String username,
                                       final AuditEventType eventType,
                                       final String description) {

        return new AuditEvent(UUID.randomUUID(),
                              userId,
                              username,
                              eventType,
                              description,
                              Instant.now());
    }

    public static AuditEvent restore(final UUID id,
                                     final Long userId,
                                     final String username,
                                     final AuditEventType eventType,
                                     final String description,
                                     final Instant createdAt) {
        if (id == null) {
            throw new InvalidAuditEventException("Audit event id is required.");
        }

        return new AuditEvent(id,
                              userId,
                              username,
                              eventType,
                              description,
                              createdAt);
    }
}