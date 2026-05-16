package com.why.buildingmanagement.chat.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        Long senderUserId,
        String senderDisplayName,
        String senderAvatarUrl,
        String content,
        String imageUrl,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        List<ChatReactionSummaryResponse> reactions) {
}