package com.why.buildingmanagement.shareandhelp.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ShareAndHelpPostResult(UUID id,
                                     UUID buildingId,
                                     String title,
                                     String description,
                                     Long createdByUserId,
                                     String createdByDisplayName,
                                     String createdByAvatarUrl,
                                     Instant createdAt,
                                     Instant updatedAt,
                                     String imageUrl,
                                     List<ShareAndHelpCommentResult> comments) {
}