package com.why.buildingmanagement.shareandhelp.application.result;

import java.time.Instant;
import java.util.UUID;

public record ShareAndHelpCommentResult(UUID id,
                                        String comment,
                                        Instant createdAt,
                                        Long createdByUserId,
                                        String createdByDisplayName,
                                        String createdByAvatarUrl) {
}