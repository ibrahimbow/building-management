package com.why.buildingmanagement.chat.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReactToChatMessageCommand(

        @NotNull(message = "message id required")
        UUID messageId,

        @NotNull(message = "user id required")
        Long userId,

        @NotBlank(message = "emoji required")
        String emoji) {
}