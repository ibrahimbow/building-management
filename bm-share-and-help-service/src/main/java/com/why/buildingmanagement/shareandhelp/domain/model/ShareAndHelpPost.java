package com.why.buildingmanagement.shareandhelp.domain.model;

import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostDeletedException;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class ShareAndHelpPost {

    private final UUID id;
    private final UUID buildingId;

    private final Long createdByUserId;
    private final String createdByDisplayName;
    private final String createdByAvatarUrl;

    private String title;
    private String description;
    private String imageUrl;

    private final Instant createdAt;

    private Instant updatedAt;
    private Instant deletedAt;

    private final List<ShareAndHelpComment> comments;

    private ShareAndHelpPost(final UUID id,
                             final UUID buildingId,
                             final Long createdByUserId,
                             final String createdByDisplayName,
                             final String createdByAvatarUrl,
                             final String title,
                             final String description,
                             final String imageUrl,
                             final Instant createdAt,
                             final Instant updatedAt,
                             final Instant deletedAt,
                             final List<ShareAndHelpComment> comments) {
        this.id = Objects.requireNonNull(id);
        this.buildingId = Objects.requireNonNull(buildingId);

        this.createdByUserId = Objects.requireNonNull(createdByUserId);
        this.createdByDisplayName = createdByDisplayName;
        this.createdByAvatarUrl = createdByAvatarUrl;

        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;

        this.createdAt = Objects.requireNonNull(createdAt);

        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;

        this.comments = new ArrayList<>(comments == null
                ? List.of()
                : comments);
    }

    public static ShareAndHelpPost createNew(final UUID buildingId,
                                             final Long createdByUserId,
                                             final String createdByDisplayName,
                                             final String createdByAvatarUrl,
                                             final String title,
                                             final String description,
                                             final String imageUrl) {
        final Instant now = Instant.now();

        return new ShareAndHelpPost(UUID.randomUUID(),
                buildingId,
                createdByUserId,
                createdByDisplayName,
                createdByAvatarUrl,
                title,
                description,
                imageUrl,
                now,
                now,
                null,
                List.of());
    }

    public static ShareAndHelpPost restore(final UUID id,
                                           final UUID buildingId,
                                           final Long createdByUserId,
                                           final String createdByDisplayName,
                                           final String createdByAvatarUrl,
                                           final String title,
                                           final String description,
                                           final String imageUrl,
                                           final Instant createdAt,
                                           final Instant updatedAt,
                                           final Instant deletedAt,
                                           final List<ShareAndHelpComment> comments) {
        return new ShareAndHelpPost(id,
                buildingId,
                createdByUserId,
                createdByDisplayName,
                createdByAvatarUrl,
                title,
                description,
                imageUrl,
                createdAt,
                updatedAt,
                deletedAt,
                comments);
    }

    public void update(final String title,
                       final String description,
                       final String imageUrl) {
        ensureNotDeleted();

        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        ensureNotDeleted();

        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void addComment(final ShareAndHelpComment comment) {
        ensureNotDeleted();

        this.comments.add(Objects.requireNonNull(comment));
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isOwnedBy(final Long userId) {
        return Objects.equals(createdByUserId, userId);
    }

    public List<ShareAndHelpComment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void deleteComment(final UUID commentId) {
        ensureNotDeleted();

        final ShareAndHelpComment comment = comments.stream()
                        .filter(currentComment -> currentComment.getId().equals(commentId))
                        .findFirst()
                        .orElseThrow(() -> new ShareAndHelpCommentNotFoundException(commentId));

        comment.delete();

        this.updatedAt = Instant.now();
    }

    private void ensureNotDeleted() {
        if (isDeleted()) {
            throw new ShareAndHelpPostDeletedException();
        }
    }
}