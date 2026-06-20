package com.why.buildingmanagement.file.domain.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(final Throwable cause) {
        super("Could not store file.", cause);
    }
}
