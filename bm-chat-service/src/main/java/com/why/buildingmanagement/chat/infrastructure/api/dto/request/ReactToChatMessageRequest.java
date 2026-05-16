package com.why.buildingmanagement.chat.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReactToChatMessageRequest(

        @NotBlank(message = "emoji required")
        String emoji) {
}