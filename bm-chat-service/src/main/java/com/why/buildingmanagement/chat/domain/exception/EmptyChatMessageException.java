package com.why.buildingmanagement.chat.domain.exception;

public class EmptyChatMessageException extends RuntimeException {

    public EmptyChatMessageException() {

        super("Chat message content or image is required");
    }
}