package com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record ShareAndHelpCommentCreatedEvent(
                UUID commentId,
                UUID postId,
                UUID buildingId,
                Long postOwnerUserId,
                Long commentCreatedByUserId,
                String postTitle,
                String commentCreatedByDisplayName,
                Instant createdAt) {
}