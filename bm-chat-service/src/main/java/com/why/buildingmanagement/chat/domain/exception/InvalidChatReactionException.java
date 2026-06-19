package com.why.buildingmanagement.chat.domain.exception;

public class InvalidChatReactionException extends RuntimeException {
    public InvalidChatReactionException(String message) {
        super(message);
    }
}
