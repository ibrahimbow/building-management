package com.why.buildingmanagement.chat.application.port.out;

import com.why.buildingmanagement.chat.domain.model.ChatMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadChatMessagePort {

    Optional<ChatMessage> findById(final UUID messageId);

    List<ChatMessage> findByBuildingIdOrderByCreatedAtAsc(final UUID buildingId);
}