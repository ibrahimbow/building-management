package com.why.buildingmanagement.notification.infrastructure.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record ShareAndHelpPostCreatedEvent(
                UUID postId,
                UUID buildingId,
                Long createdByUserId,
                String title,
                String createdByDisplayName,
                Instant createdAt) {
}