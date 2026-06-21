package com.why.buildingmanagement.announcement.domain.validation;

import com.why.buildingmanagement.announcement.domain.exception.InvalidAnnouncementException;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;

import java.util.UUID;

public final class AnnouncementValidator {

    private static final int MAX_CREATED_BY_LENGTH = 255;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_MESSAGE_LENGTH = 5_000;
    private static final int MAX_IMAGE_URL_LENGTH = 500;

    private AnnouncementValidator() {
    }

    public static UUID validateBuildingId(final UUID buildingId) {

        if (buildingId == null) {
            throw new InvalidAnnouncementException("Announcement building id is required");
        }

        return buildingId;
    }

    public static Long validateManagerId(final Long managerId) {

        if (managerId == null) {
            throw new InvalidAnnouncementException("Announcement manager id is required");
        }

        return managerId;
    }

    public static String validateCreatedBy(final String createdBy) {

        if (createdBy == null || createdBy.isBlank()) {
            throw new InvalidAnnouncementException("Announcement creator is required");
        }

        final String trimmedCreatedBy = createdBy.trim();

        if (trimmedCreatedBy.length() > MAX_CREATED_BY_LENGTH) {
            throw new InvalidAnnouncementException("Announcement creator cannot exceed "
                                                                   + MAX_CREATED_BY_LENGTH
                                                                   + " characters");
        }

        return trimmedCreatedBy;
    }

    public static String validateTitle(final String title) {

        if (title == null || title.isBlank()) {
            throw new InvalidAnnouncementException("Announcement title is required");
        }

        final String trimmedTitle = title.trim();

        if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
            throw new InvalidAnnouncementException("Announcement title cannot exceed "
                                                                   + MAX_TITLE_LENGTH
                                                                   + " characters");
        }

        return trimmedTitle;
    }

    public static String validateMessage(final String message) {

        if (message == null || message.isBlank()) {
            throw new InvalidAnnouncementException("Announcement message is required");
        }

        final String trimmedMessage = message.trim();

        if (trimmedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new InvalidAnnouncementException("Announcement message cannot exceed "
                                                                   + MAX_MESSAGE_LENGTH
                                                                   + " characters");
        }

        return trimmedMessage;
    }

    public static AnnouncementCategory validateCategory(final AnnouncementCategory category) {

        if (category == null) {
            throw new InvalidAnnouncementException("Announcement category is required");
        }

        return category;
    }

    public static String validateImageUrl(final String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        final String trimmedImageUrl = imageUrl.trim();

        if (trimmedImageUrl.length() > MAX_IMAGE_URL_LENGTH) {
            throw new InvalidAnnouncementException("Announcement image url cannot exceed "
                                                                   + MAX_IMAGE_URL_LENGTH
                                                                   + " characters");
        }

        return trimmedImageUrl;
    }
}