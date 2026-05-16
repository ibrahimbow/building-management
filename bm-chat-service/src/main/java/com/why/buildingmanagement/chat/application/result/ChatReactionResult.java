package com.why.buildingmanagement.chat.application.result;

import java.time.Instant;
import java.util.UUID;

public record ChatReactionResult(
        UUID id,
        UUID messageId,
        Long userId,
        String emoji,
        Instant createdAt) {
}