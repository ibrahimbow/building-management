package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteShareAndHelpPostCommand(

        @NotNull(message = "post id required")
        UUID postId,

        @NotNull(message = "user id required")
        Long userId) {
}