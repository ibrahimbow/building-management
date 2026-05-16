package com.why.buildingmanagement.chat.domain.exception;

import java.util.UUID;

public class ChatMessageNotFoundException extends RuntimeException {

    public ChatMessageNotFoundException(final UUID messageId) {

        super("Chat message not found with id: " + messageId);
    }
}