package com.why.buildingmanagement.audit.infrastructure.security.exception;

public class MissingUserHeaderException extends RuntimeException {

    public MissingUserHeaderException(final String headerName) {
        super("Missing required user header: " + headerName);
    }
}
