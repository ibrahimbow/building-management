package com.why.buildingmanagement.notification.application.result;

import com.why.buildingmanagement.notification.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResult(
                UUID id,
                Long userId,
                UUID buildingId,
                NotificationType type,
                String title,
                String message,
                boolean read,
                Instant createdAt,
                Instant readAt) {
}