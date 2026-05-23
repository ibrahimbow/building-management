package com.why.buildingmanagement.notification.infrastructure.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record AnnouncementCreatedEvent(
                UUID announcementId,
                UUID buildingId,
                String title,
                String category,
                String createdByDisplayName,
                Instant createdAt) {
}