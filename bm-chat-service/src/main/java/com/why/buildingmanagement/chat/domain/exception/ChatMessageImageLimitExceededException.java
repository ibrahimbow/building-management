package com.why.buildingmanagement.chat.domain.exception;

public class ChatMessageImageLimitExceededException extends RuntimeException {

    public ChatMessageImageLimitExceededException() {

        super("Only one image is allowed per chat message");
    }
}