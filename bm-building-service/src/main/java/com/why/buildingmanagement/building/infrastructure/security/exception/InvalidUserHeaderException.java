package com.why.buildingmanagement.building.infrastructure.security.exception;

public class InvalidUserHeaderException extends RuntimeException {
    public InvalidUserHeaderException(final String headerName) {
        super("Invalid user header: " + headerName);
    }
}
