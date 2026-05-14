package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteCommentCommand(

        @NotNull(message = "post id required")
        UUID postId,

        @NotNull(message = "comment id required")
        UUID commentId,

        @NotNull(message = "user id required")
        Long userId) {
}