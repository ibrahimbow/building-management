package com.why.buildingmanagement.notification.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Notification {

    private UUID id;
    private Long userId;
    private UUID buildingId;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;
    private Instant readAt;

    private Notification(final UUID id,
                         final Long userId,
                         final UUID buildingId,
                         final NotificationType type,
                         final String title,
                         final String message,
                         final boolean read,
                         final Instant createdAt,
                         final Instant readAt) {

        this.id = id;
        this.userId = userId;
        this.buildingId = buildingId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public static Notification createNew(final Long userId,
                                         final UUID buildingId,
                                         final NotificationType type,
                                         final String title,
                                         final String message) {

        return new Notification(
                        null,
                        userId,
                        buildingId,
                        type,
                        title,
                        message,
                        false,
                        Instant.now(),
                        null);
    }

    public static Notification restore(final UUID id,
                                       final Long userId,
                                       final UUID buildingId,
                                       final NotificationType type,
                                       final String title,
                                       final String message,
                                       final boolean read,
                                       final Instant createdAt,
                                       final Instant readAt) {

        return new Notification(
                        id,
                        userId,
                        buildingId,
                        type,
                        title,
                        message,
                        read,
                        createdAt,
                        readAt);
    }

    public void markAsRead() {

        this.read = true;
        this.readAt = Instant.now();
    }
}