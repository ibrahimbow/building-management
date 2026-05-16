package com.why.buildingmanagement.chat.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendChatMessageCommand(

        Long senderUserId,

        @NotBlank(message = "sender display name required")
        String senderDisplayName,

        @Size(max = 500, message = "sender avatar url must not exceed 500 characters")
        String senderAvatarUrl,

        @Size(max = 2000, message = "message content must not exceed 2000 characters")
        String content,

        @Size(max = 500, message = "image url must not exceed 500 characters")
        String imageUrl) {
}