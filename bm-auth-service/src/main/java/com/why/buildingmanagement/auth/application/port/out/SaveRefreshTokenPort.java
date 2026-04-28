package com.why.buildingmanagement.auth.application.port.out;

import com.why.buildingmanagement.auth.domain.model.RefreshToken;

public interface SaveRefreshTokenPort {
    RefreshToken save(RefreshToken token);
}
