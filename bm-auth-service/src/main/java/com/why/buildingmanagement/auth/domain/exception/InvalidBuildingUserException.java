package com.why.buildingmanagement.auth.domain.exception;

public class InvalidBuildingUserException extends RuntimeException {

    public InvalidBuildingUserException(final String message) {
        super(message);
    }
}