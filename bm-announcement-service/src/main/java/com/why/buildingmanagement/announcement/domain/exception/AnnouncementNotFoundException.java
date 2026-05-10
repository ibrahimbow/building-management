package com.why.buildingmanagement.announcement.domain.exception;

import java.util.UUID;

public class AnnouncementNotFoundException extends RuntimeException {

    public AnnouncementNotFoundException(final UUID announcementId) {
        super("Announcement not found: " + announcementId);
    }
}