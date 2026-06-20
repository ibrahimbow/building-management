package com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ShareAndHelpCommentResponse(
        UUID id,
        Long createdByUserId,
        String createdByDisplayName,
        String createdByAvatarUrl,
        String comment,
        Instant createdAt) {
}