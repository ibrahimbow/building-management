package com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPostStatus;

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
        ShareAndHelpPostStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<ShareAndHelpCommentResponse> comments) {
}