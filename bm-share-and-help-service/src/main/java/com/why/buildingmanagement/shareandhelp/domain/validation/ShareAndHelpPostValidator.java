package com.why.buildingmanagement.shareandhelp.domain.validation;

import com.why.buildingmanagement.shareandhelp.domain.exception.InvalidShareAndHelpPostException;

import java.time.Instant;
import java.util.UUID;

public final class ShareAndHelpPostValidator {

    private static final int MAX_DISPLAY_NAME_LENGTH = 500;
    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_DESCRIPTION_LENGTH = 5_000;
    private static final int MAX_IMAGE_URL_LENGTH = 500;
    private static final int MAX_AVATAR_URL_LENGTH = 500;

    private ShareAndHelpPostValidator() {
    }

    public static UUID validateId(final UUID id) {
        if (id == null) {
            throw new InvalidShareAndHelpPostException("Post id is required");
        }

        return id;
    }

    public static UUID validateBuildingId(final UUID buildingId) {
        if (buildingId == null) {
            throw new InvalidShareAndHelpPostException("Building id is required");
        }

        return buildingId;
    }

    public static Long validateCreatedByUserId(final Long createdByUserId) {
        if (createdByUserId == null) {
            throw new InvalidShareAndHelpPostException("Post creator user id is required");
        }

        return createdByUserId;
    }

    public static String validateDisplayName(final String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidShareAndHelpPostException("Post creator display name is required");
        }

        final String trimmedDisplayName = displayName.trim();

        if (trimmedDisplayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new InvalidShareAndHelpPostException("Post creator display name cannot exceed 500 characters");
        }

        return trimmedDisplayName;
    }

    public static String validateAvatarUrl(final String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return null;
        }

        final String trimmedAvatarUrl = avatarUrl.trim();

        if (trimmedAvatarUrl.length() > MAX_AVATAR_URL_LENGTH) {
            throw new InvalidShareAndHelpPostException("Post creator avatar url cannot exceed 500 characters");
        }

        return trimmedAvatarUrl;
    }

    public static String validateTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidShareAndHelpPostException("Post title is required");
        }

        final String trimmedTitle = title.trim();

        if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
            throw new InvalidShareAndHelpPostException("Post title cannot exceed 150 characters");
        }

        return trimmedTitle;
    }

    public static String validateDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidShareAndHelpPostException("Post description is required");
        }

        final String trimmedDescription = description.trim();

        if (trimmedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidShareAndHelpPostException("Post description cannot exceed 5000 characters");
        }

        return trimmedDescription;
    }

    public static String validateImageUrl(final String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        final String trimmedImageUrl = imageUrl.trim();

        if (trimmedImageUrl.length() > MAX_IMAGE_URL_LENGTH) {
            throw new InvalidShareAndHelpPostException("Post image url cannot exceed 500 characters");
        }

        return trimmedImageUrl;
    }

    public static Instant validateCreatedAt(final Instant createdAt) {
        if (createdAt == null) {
            throw new InvalidShareAndHelpPostException("Post creation date is required");
        }

        return createdAt;
    }

    public static Instant validateUpdatedAt(final Instant updatedAt) {
        if (updatedAt == null) {
            throw new InvalidShareAndHelpPostException("Post update date is required");
        }

        return updatedAt;
    }
}