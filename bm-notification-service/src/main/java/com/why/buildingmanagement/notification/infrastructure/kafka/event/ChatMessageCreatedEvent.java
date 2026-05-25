package com.why.buildingmanagement.notification.infrastructure.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageCreatedEvent(
                UUID messageId,
                UUID buildingId,
                Long senderUserId,
                String senderDisplayName,
                String content,
                String imageUrl,
                Instant createdAt) {
}