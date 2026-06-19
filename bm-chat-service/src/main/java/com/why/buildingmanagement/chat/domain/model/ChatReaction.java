package com.why.buildingmanagement.chat.domain.model;

import com.why.buildingmanagement.chat.domain.exception.InvalidChatReactionException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ChatReaction {

    private final UUID id;
    private final UUID messageId;
    private final Long userId;
    private final String emoji;
    private final Instant createdAt;

    private ChatReaction(final UUID id,
                         final UUID messageId,
                         final Long userId,
                         final String emoji,
                         final Instant createdAt) {

        validateRequired(id, messageId, userId, emoji, createdAt);

        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.emoji = emoji.trim();
        this.createdAt = createdAt;
    }

    public static ChatReaction createNew(final UUID messageId,
                                         final Long userId,
                                         final String emoji) {

        return new ChatReaction(
                        UUID.randomUUID(),
                        messageId,
                        userId,
                        emoji,
                        Instant.now());
    }

    public static ChatReaction restore(final UUID id,
                                       final UUID messageId,
                                       final Long userId,
                                       final String emoji,
                                       final Instant createdAt) {

        return new ChatReaction(
                        id,
                        messageId,
                        userId,
                        emoji,
                        createdAt);
    }

    private static void validateRequired(final UUID id,
                                         final UUID messageId,
                                         final Long userId,
                                         final String emoji,
                                         final Instant createdAt) {

        if (id == null) {
            throw new InvalidChatReactionException("Reaction id is required.");
        }

        if (messageId == null) {
            throw new InvalidChatReactionException("Message id is required.");
        }

        if (userId == null) {
            throw new InvalidChatReactionException("User id is required.");
        }

        if (emoji == null || emoji.isBlank()) {
            throw new InvalidChatReactionException("Emoji is required.");
        }

        if (createdAt == null) {
            throw new InvalidChatReactionException("Created date is required.");
        }
    }
}