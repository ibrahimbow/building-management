package com.why.buildingmanagement.shareandhelp.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ShareAndHelpPostResult(UUID id,
                                     String title,
                                     String description,
                                     Instant createdAt,
                                     Long createdByUserId,
                                     String createdByDisplayName,
                                     String createdByAvatarUrl,
                                     List<String> images,
                                     List<ShareAndHelpCommentResult> comments) {
}