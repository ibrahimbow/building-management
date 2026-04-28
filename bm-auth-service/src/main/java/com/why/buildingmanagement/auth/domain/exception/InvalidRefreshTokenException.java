package com.why.buildingmanagement.auth.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super("Invalid refresh token");
    }
}
