package com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ShareAndHelpPostResponse(
        UUID id,
        Long createdByUserId,
        String createdByDisplayName,
        String createdByAvatarUrl,
        String title,
        String description,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt,
        List<ShareAndHelpCommentResponse> comments) {
}