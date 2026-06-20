package com.why.buildingmanagement.shareandhelp.domain.validation;

import com.why.buildingmanagement.shareandhelp.domain.exception.InvalidShareAndHelpCommentException;

import java.time.Instant;
import java.util.UUID;

public final class ShareAndHelpCommentValidator {

    private static final int MAX_DISPLAY_NAME_LENGTH = 100;
    private static final int MAX_AVATAR_URL_LENGTH = 500;
    private static final int MAX_COMMENT_LENGTH = 2_000;

    private ShareAndHelpCommentValidator() {
    }

    public static UUID validateId(final UUID id) {
        if (id == null) {
            throw new InvalidShareAndHelpCommentException("Comment id is required");
        }

        return id;
    }

    public static UUID validatePostId(final UUID postId) {
        if (postId == null) {
            throw new InvalidShareAndHelpCommentException("Post id is required");
        }

        return postId;
    }

    public static Long validateCreatedByUserId(final Long createdByUserId) {
        if (createdByUserId == null) {
            throw new InvalidShareAndHelpCommentException("Comment creator user id is required");
        }

        return createdByUserId;
    }

    public static String validateDisplayName(final String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidShareAndHelpCommentException("Comment creator display name is required");
        }

        final String trimmedDisplayName = displayName.trim();

        if (trimmedDisplayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new InvalidShareAndHelpCommentException("Comment creator display name cannot exceed 100 characters");
        }

        return trimmedDisplayName;
    }

    public static String validateAvatarUrl(final String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return null;
        }

        final String trimmedAvatarUrl = avatarUrl.trim();

        if (trimmedAvatarUrl.length() > MAX_AVATAR_URL_LENGTH) {
            throw new InvalidShareAndHelpCommentException("Comment creator avatar url cannot exceed 500 characters");
        }

        return trimmedAvatarUrl;
    }

    public static String validateComment(final String comment) {
        if (comment == null || comment.isBlank()) {
            throw new InvalidShareAndHelpCommentException("Comment cannot be blank");
        }

        final String trimmedComment = comment.trim();

        if (trimmedComment.length() > MAX_COMMENT_LENGTH) {
            throw new InvalidShareAndHelpCommentException("Comment cannot exceed 1000 characters");
        }

        return trimmedComment;
    }

    public static Instant validateCreatedAt(final Instant createdAt) {
        if (createdAt == null) {
            throw new InvalidShareAndHelpCommentException("Comment creation date is required");
        }

        return createdAt;
    }
}