package com.why.buildingmanagement.notification.domain.exception;

public class NotificationAlreadyReadException extends RuntimeException {
    public NotificationAlreadyReadException(String message) {
        super(message);
    }
}
