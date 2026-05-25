package com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event;

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