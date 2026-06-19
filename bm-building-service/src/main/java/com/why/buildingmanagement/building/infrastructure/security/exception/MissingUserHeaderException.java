package com.why.buildingmanagement.building.infrastructure.security.exception;

public class MissingUserHeaderException extends RuntimeException {
    public MissingUserHeaderException(final String headerName) {
        super("Missing required user header: " + headerName);
    }
}
