package com.why.buildingmanagement.chat.application.port.out;

import com.why.buildingmanagement.chat.domain.model.ChatReaction;

import java.util.List;
import java.util.UUID;

public interface LoadChatReactionPort {

    boolean existsByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                               final Long userId,
                                               final String emoji);

    List<ChatReaction> findByMessageIdIn(final List<UUID> messageIds);
}