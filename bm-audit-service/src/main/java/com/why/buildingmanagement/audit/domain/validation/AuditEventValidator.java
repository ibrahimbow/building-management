package com.why.buildingmanagement.audit.domain.validation;

import com.why.buildingmanagement.audit.domain.exception.InvalidAuditEventException;
import com.why.buildingmanagement.audit.domain.model.AuditEventType;

import java.time.Instant;

public final class AuditEventValidator {

    private AuditEventValidator() {
    }

    public static void validate(final AuditEventType eventType,
                                final String description,
                                final Instant createdAt) {

        if (eventType == null) {
            throw new InvalidAuditEventException("Audit event type is required.");
        }

        if (description == null || description.isBlank()) {
            throw new InvalidAuditEventException("Audit event description is required.");
        }

        if (description.length() > 1000) {
            throw new InvalidAuditEventException("Audit event description must not exceed 1000 characters.");
        }

        if (createdAt == null) {
            throw new InvalidAuditEventException("Audit event creation date is required.");
        }
    }
}