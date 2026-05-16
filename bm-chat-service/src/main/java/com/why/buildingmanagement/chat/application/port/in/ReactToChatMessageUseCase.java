package com.why.buildingmanagement.chat.application.port.in;

import com.why.buildingmanagement.chat.application.result.ChatReactionResult;

public interface ReactToChatMessageUseCase {

    ChatReactionResult react(final ReactToChatMessageCommand command);

    void removeReaction(final ReactToChatMessageCommand command);
}