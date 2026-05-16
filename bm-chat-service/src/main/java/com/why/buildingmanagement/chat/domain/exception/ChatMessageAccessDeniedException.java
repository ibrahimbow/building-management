package com.why.buildingmanagement.chat.domain.exception;

import java.util.UUID;

public class ChatMessageAccessDeniedException extends RuntimeException {

    public ChatMessageAccessDeniedException(final UUID messageId,
                                            final Long userId) {

        super("User " + userId
                + " is not allowed to access chat message "
                + messageId);
    }
}