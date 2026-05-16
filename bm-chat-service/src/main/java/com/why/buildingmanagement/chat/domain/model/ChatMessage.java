package com.why.buildingmanagement.chat.domain.model;

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

    private String content;
    private String imageUrl;

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

        this.id = id;
        this.buildingId = buildingId;

        this.senderUserId = senderUserId;
        this.senderDisplayName = senderDisplayName;
        this.senderAvatarUrl = senderAvatarUrl;

        this.content = content;
        this.imageUrl = imageUrl;

        this.createdAt = createdAt;

        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;

        this.reactions = reactions;
    }

    public static ChatMessage createNew(final UUID buildingId,
                                        final Long senderUserId,
                                        final String senderDisplayName,
                                        final String senderAvatarUrl,
                                        final String content,
                                        final String imageUrl) {

        return new ChatMessage(
                UUID.randomUUID(),
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

        return new ChatMessage(
                id,
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

    public void delete() {

        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {

        return deletedAt != null;
    }

    public boolean isOwnedBy(final Long userId) {

        return senderUserId.equals(userId);
    }
}