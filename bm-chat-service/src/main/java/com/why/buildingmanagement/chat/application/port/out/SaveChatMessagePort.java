package com.why.buildingmanagement.chat.application.port.out;

import com.why.buildingmanagement.chat.domain.model.ChatMessage;

public interface SaveChatMessagePort {

    ChatMessage save(final ChatMessage message);
}