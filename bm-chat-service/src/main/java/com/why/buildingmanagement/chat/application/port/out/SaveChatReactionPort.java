package com.why.buildingmanagement.chat.application.port.out;

import com.why.buildingmanagement.chat.domain.model.ChatReaction;

public interface SaveChatReactionPort {

    ChatReaction save(final ChatReaction reaction);
}