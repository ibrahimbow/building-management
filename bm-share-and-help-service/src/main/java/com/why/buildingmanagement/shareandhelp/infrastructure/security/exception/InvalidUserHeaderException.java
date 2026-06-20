package com.why.buildingmanagement.shareandhelp.infrastructure.security.exception;


public class InvalidUserHeaderException extends RuntimeException {
    public InvalidUserHeaderException(final String headerName, final String value) {
        super("Invalid user header value for " + headerName + ": " + value);
    }
}
