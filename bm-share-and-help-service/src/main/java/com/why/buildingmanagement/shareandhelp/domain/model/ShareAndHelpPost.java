package com.why.buildingmanagement.shareandhelp.domain.model;

import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostDeletedException;
import com.why.buildingmanagement.shareandhelp.domain.validation.ShareAndHelpPostValidator;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

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
        this.id = ShareAndHelpPostValidator.validateId(id);
        this.buildingId = ShareAndHelpPostValidator.validateBuildingId(buildingId);
        this.createdByUserId = ShareAndHelpPostValidator.validateCreatedByUserId(createdByUserId);
        this.createdByDisplayName = ShareAndHelpPostValidator.validateDisplayName(createdByDisplayName);
        this.createdByAvatarUrl = ShareAndHelpPostValidator.validateAvatarUrl(createdByAvatarUrl);
        this.title = ShareAndHelpPostValidator.validateTitle(title);
        this.description = ShareAndHelpPostValidator.validateDescription(description);
        this.imageUrl = ShareAndHelpPostValidator.validateImageUrl(imageUrl);
        this.createdAt = ShareAndHelpPostValidator.validateCreatedAt(createdAt);
        this.updatedAt = ShareAndHelpPostValidator.validateUpdatedAt(updatedAt);
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

        this.title = ShareAndHelpPostValidator.validateTitle(title);
        this.description = ShareAndHelpPostValidator.validateDescription(description);
        this.imageUrl = ShareAndHelpPostValidator.validateImageUrl(imageUrl);
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