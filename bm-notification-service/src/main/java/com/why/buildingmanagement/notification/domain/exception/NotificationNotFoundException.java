package com.why.buildingmanagement.notification.domain.exception;

import java.util.UUID;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(final UUID notificationId) {

        super("Notification not found with id: " + notificationId);
    }
}