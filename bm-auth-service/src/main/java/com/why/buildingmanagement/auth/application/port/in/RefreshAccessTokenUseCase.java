package com.why.buildingmanagement.auth.application.port.in;

import com.why.buildingmanagement.auth.application.result.LoginResult;

public interface RefreshAccessTokenUseCase {
    LoginResult refresh(RefreshAccessTokenCommand command);
}
