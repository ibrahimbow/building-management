package com.why.buildingmanagement.auth.domain.exception;

public class ExpiredOrRevokedRefreshTokenException extends RuntimeException {
    public ExpiredOrRevokedRefreshTokenException(String message) {
        super("Refresh token is expired or revoked");
    }
}
