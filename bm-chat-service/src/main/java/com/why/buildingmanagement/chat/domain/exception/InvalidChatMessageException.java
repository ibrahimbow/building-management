package com.why.buildingmanagement.chat.domain.exception;

public class InvalidChatMessageException extends RuntimeException {
    public InvalidChatMessageException(String message) {
        super(message);
    }
}
