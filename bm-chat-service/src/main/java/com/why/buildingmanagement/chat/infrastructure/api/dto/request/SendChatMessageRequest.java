package com.why.buildingmanagement.chat.infrastructure.api.dto.request;

import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(

        @Size(max = 2000, message = "message content must not exceed 2000 characters")
        String content,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}