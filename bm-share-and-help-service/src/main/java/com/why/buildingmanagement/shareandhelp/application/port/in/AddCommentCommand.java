package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddCommentCommand(

        @NotNull(message = "post id required")
        UUID postId,

        @NotNull(message = "created by user id required")
        Long createdByUserId,

        @NotBlank(message = "created by display name required")
        String createdByDisplayName,

        @Size(max = 500, message = "created by avatar url must not exceed 500 characters")
        String createdByAvatarUrl,

        @NotBlank(message = "comment required")
        @Size(max = 2000, message = "comment must not exceed 2000 characters")
        String comment) {
}