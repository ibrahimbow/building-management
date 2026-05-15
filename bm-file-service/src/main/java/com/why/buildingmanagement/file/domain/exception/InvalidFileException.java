package com.why.buildingmanagement.file.domain.exception;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException(final String message) {
        super(message);
    }
}