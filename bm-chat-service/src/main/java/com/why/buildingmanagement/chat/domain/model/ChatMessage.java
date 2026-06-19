package com.why.buildingmanagement.chat.domain.model;

import com.why.buildingmanagement.chat.domain.exception.InvalidChatMessageException;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ChatMessage {

    private final UUID id;
    private final UUID buildingId;
    private final Long senderUserId;
    private final String senderDisplayName;
    private final String senderAvatarUrl;
    private final String content;
    private final String imageUrl;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private final List<ChatReaction> reactions;

    private ChatMessage(final UUID id,
                        final UUID buildingId,
                        final Long senderUserId,
                        final String senderDisplayName,
                        final String senderAvatarUrl,
                        final String content,
                        final String imageUrl,
                        final Instant createdAt,
                        final Instant updatedAt,
                        final Instant deletedAt,
                        final List<ChatReaction> reactions) {

        validateRequired(id, buildingId, senderUserId, senderDisplayName, createdAt);
        validateMessageBody(content, imageUrl);

        this.id = id;
        this.buildingId = buildingId;
        this.senderUserId = senderUserId;
        this.senderDisplayName = senderDisplayName.trim();
        this.senderAvatarUrl = normalizeNullableText(senderAvatarUrl);
        this.content = normalizeNullableText(content);
        this.imageUrl = normalizeNullableText(imageUrl);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.reactions = reactions == null
                        ? new ArrayList<>()
                        : new ArrayList<>(reactions);
    }

    public static ChatMessage createNew(final UUID buildingId,
                                        final Long senderUserId,
                                        final String senderDisplayName,
                                        final String senderAvatarUrl,
                                        final String content,
                                        final String imageUrl) {

        return new ChatMessage(UUID.randomUUID(),
                               buildingId,
                               senderUserId,
                               senderDisplayName,
                               senderAvatarUrl,
                               content,
                               imageUrl,
                               Instant.now(),
                               null,
                               null,
                               new ArrayList<>());
    }

    public static ChatMessage restore(final UUID id,
                                      final UUID buildingId,
                                      final Long senderUserId,
                                      final String senderDisplayName,
                                      final String senderAvatarUrl,
                                      final String content,
                                      final String imageUrl,
                                      final Instant createdAt,
                                      final Instant updatedAt,
                                      final Instant deletedAt,
                                      final List<ChatReaction> reactions) {

        return new ChatMessage(id,
                               buildingId,
                               senderUserId,
                               senderDisplayName,
                               senderAvatarUrl,
                               content,
                               imageUrl,
                               createdAt,
                               updatedAt,
                               deletedAt,
                               reactions);
    }

    public List<ChatReaction> getReactions() {
        return List.copyOf(reactions);
    }

    public void delete() {

        if (isDeleted()) {
            return;
        }

        final Instant now = Instant.now();

        deletedAt = now;
        updatedAt = now;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isOwnedBy(final Long userId) {

        return userId != null && senderUserId.equals(userId);
    }

    private static void validateRequired(final UUID id,
                                         final UUID buildingId,
                                         final Long senderUserId,
                                         final String senderDisplayName,
                                         final Instant createdAt) {

        if (id == null) {
            throw new InvalidChatMessageException("Chat message id is required.");
        }

        if (buildingId == null) {
            throw new InvalidChatMessageException("Building id is required.");
        }

        if (senderUserId == null) {
            throw new InvalidChatMessageException("Sender user id is required.");
        }

        if (senderDisplayName == null || senderDisplayName.isBlank()) {
            throw new InvalidChatMessageException("Sender display name is required.");
        }

        if (createdAt == null) {
            throw new InvalidChatMessageException("Created date is required.");
        }
    }

    private static void validateMessageBody(final String content, final String imageUrl) {

        if ((content == null || content.isBlank()) && (imageUrl == null || imageUrl.isBlank())) {
            throw new InvalidChatMessageException("Message content or image is required.");
        }
    }

    private static String normalizeNullableText(final String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}