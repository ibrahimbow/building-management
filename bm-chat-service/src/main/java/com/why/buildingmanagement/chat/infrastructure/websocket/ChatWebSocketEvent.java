package com.why.buildingmanagement.chat.infrastructure.websocket;

import com.why.buildingmanagement.chat.application.result.ChatMessageResult;

import java.util.UUID;

public record ChatWebSocketEvent(
        ChatWebSocketEventType type,
        UUID buildingId,
        ChatMessageResult message) {
}