package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResolveShareAndHelpPostCommand(

                @NotNull(message = "post id is required")
                UUID postId,

                @NotNull(message = "building id is required")
                UUID buildingId,

                @NotNull(message = "current user id is required")
                Long currentUserId) {
}
