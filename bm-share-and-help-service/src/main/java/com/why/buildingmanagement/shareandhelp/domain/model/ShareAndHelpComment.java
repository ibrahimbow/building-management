package com.why.buildingmanagement.shareandhelp.domain.model;

import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentAlreadyDeletedException;
import com.why.buildingmanagement.shareandhelp.domain.validation.ShareAndHelpCommentValidator;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class ShareAndHelpComment {

    private final UUID id;
    private final UUID postId;

    private final Long createdByUserId;
    private final String createdByDisplayName;
    private final String createdByAvatarUrl;

    private final String comment;

    private final Instant createdAt;

    private Instant deletedAt;

    private ShareAndHelpComment(final UUID id,
                                final UUID postId,
                                final Long createdByUserId,
                                final String createdByDisplayName,
                                final String createdByAvatarUrl,
                                final String comment,
                                final Instant createdAt,
                                final Instant deletedAt) {

        this.id = ShareAndHelpCommentValidator.validateId(id);
        this.postId = ShareAndHelpCommentValidator.validatePostId(postId);
        this.createdByUserId = ShareAndHelpCommentValidator.validateCreatedByUserId(createdByUserId);
        this.createdByDisplayName = ShareAndHelpCommentValidator.validateDisplayName(createdByDisplayName);
        this.createdByAvatarUrl = ShareAndHelpCommentValidator.validateAvatarUrl(createdByAvatarUrl);
        this.comment = ShareAndHelpCommentValidator.validateComment(comment);
        this.createdAt = ShareAndHelpCommentValidator.validateCreatedAt(createdAt);
        this.deletedAt = deletedAt;
    }

    public static ShareAndHelpComment createNew(final UUID postId,
                                                final Long createdByUserId,
                                                final String createdByDisplayName,
                                                final String createdByAvatarUrl,
                                                final String comment) {
        return new ShareAndHelpComment(UUID.randomUUID(),
                                       postId,
                                       createdByUserId,
                                       createdByDisplayName,
                                       createdByAvatarUrl,
                                       comment,
                                       Instant.now(),
                                       null);
    }

    public static ShareAndHelpComment restore(final UUID id,
                                              final UUID postId,
                                              final Long createdByUserId,
                                              final String createdByDisplayName,
                                              final String createdByAvatarUrl,
                                              final String comment,
                                              final Instant createdAt,
                                              final Instant deletedAt) {
        return new ShareAndHelpComment(id,
                                       postId,
                                       createdByUserId,
                                       createdByDisplayName,
                                       createdByAvatarUrl,
                                       comment,
                                       createdAt,
                                       deletedAt);
    }

    public void delete() {
        if (isDeleted()) {
            throw new ShareAndHelpCommentAlreadyDeletedException();
        }

        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isOwnedBy(final Long userId) {
        return Objects.equals(createdByUserId, userId);
    }
}