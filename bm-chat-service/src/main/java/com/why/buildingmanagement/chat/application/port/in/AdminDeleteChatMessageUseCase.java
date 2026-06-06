package com.why.buildingmanagement.chat.application.port.in;

import java.util.UUID;

public interface AdminDeleteChatMessageUseCase {

    void deleteMessageByAdmin(UUID messageId);
}