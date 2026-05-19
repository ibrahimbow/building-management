package com.why.buildingmanagement.chat.application.port.in;

import com.why.buildingmanagement.chat.application.result.ChatMessageResult;

public interface SendChatMessageUseCase {

    ChatMessageResult send(final SendChatMessageCommand command);

    ChatMessageResult sendFromCurrentManagerBuilding(final SendChatMessageCommand command);

}