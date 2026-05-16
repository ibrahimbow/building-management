package com.why.buildingmanagement.chat.application.port.in;

import java.util.UUID;

public interface DeleteChatMessageUseCase {

    void delete(final UUID messageId, final Long currentUserId);
}