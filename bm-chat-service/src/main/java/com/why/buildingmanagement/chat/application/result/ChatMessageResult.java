package com.why.buildingmanagement.chat.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatMessageResult(
        UUID id,
        UUID buildingId,
        Long senderUserId,
        String senderDisplayName,
        String senderAvatarUrl,
        String content,
        String imageUrl,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        List<ChatReactionSummaryResult> reactions) {
}