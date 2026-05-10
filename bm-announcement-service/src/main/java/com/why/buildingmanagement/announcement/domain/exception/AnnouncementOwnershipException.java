package com.why.buildingmanagement.announcement.domain.exception;

import java.util.UUID;

public class AnnouncementOwnershipException extends RuntimeException {

    public AnnouncementOwnershipException(final UUID announcementId) {
        super("Manager is not allowed to manage announcement: " + announcementId);
    }
}