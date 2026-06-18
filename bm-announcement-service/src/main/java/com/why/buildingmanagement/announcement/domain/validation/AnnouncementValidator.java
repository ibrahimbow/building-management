package com.why.buildingmanagement.announcement.domain.validation;

import com.why.buildingmanagement.announcement.domain.exception.InvalidAnnouncementException;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;

import java.util.UUID;

public final class AnnouncementValidator {

    private AnnouncementValidator() {
    }

    public static void validateBuildingId(final UUID buildingId) {
        if (buildingId == null) {
            throw new InvalidAnnouncementException("Announcement building id is required");
        }
    }

    public static void validateManagerId(final Long managerId) {
        if (managerId == null) {
            throw new InvalidAnnouncementException("Announcement manager id is required");
        }
    }

    public static void validateCreatedBy(final String createdBy) {
        if (createdBy == null || createdBy.isBlank()) {
            throw new InvalidAnnouncementException("Announcement created by is required");
        }

        if (createdBy.length() > 255) {
            throw new InvalidAnnouncementException("Announcement created by must not exceed 255 characters");
        }
    }

    public static void validateTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidAnnouncementException("Announcement title is required");
        }

        if (title.length() > 255) {
            throw new InvalidAnnouncementException("Announcement title must not exceed 255 characters");
        }
    }

    public static void validateMessage(final String message) {
        if (message == null || message.isBlank()) {
            throw new InvalidAnnouncementException("Announcement message is required");
        }

        if (message.length() > 5000) {
            throw new InvalidAnnouncementException("Announcement message must not exceed 5000 characters");
        }
    }

    public static void validateCategory(final AnnouncementCategory category) {
        if (category == null) {
            throw new InvalidAnnouncementException("Announcement category is required");
        }
    }

    public static void validateImageUrl(final String imageUrl) {
        if (imageUrl != null && imageUrl.length() > 500) {
            throw new InvalidAnnouncementException("Announcement image URL must not exceed 500 characters");
        }
    }
}