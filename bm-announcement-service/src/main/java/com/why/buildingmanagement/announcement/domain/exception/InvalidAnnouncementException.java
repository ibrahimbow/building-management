package com.why.buildingmanagement.announcement.domain.exception;

public class InvalidAnnouncementException extends RuntimeException {
    public InvalidAnnouncementException(String message) {
        super(message);
    }
}
