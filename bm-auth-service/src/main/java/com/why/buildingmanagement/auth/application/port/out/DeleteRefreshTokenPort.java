package com.why.buildingmanagement.auth.application.port.out;

public interface DeleteRefreshTokenPort {
    void deleteByUserId(Long userId);
}
