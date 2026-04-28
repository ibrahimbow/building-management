package com.why.buildingmanagement.auth.infrastructure.api.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public AuthResponse(final String accessToken, final String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
