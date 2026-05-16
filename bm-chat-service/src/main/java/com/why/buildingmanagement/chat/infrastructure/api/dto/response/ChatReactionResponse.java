package com.why.buildingmanagement.chat.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ChatReactionResponse(
        UUID id,
        UUID messageId,
        Long userId,
        String emoji,
        Instant createdAt) {
}