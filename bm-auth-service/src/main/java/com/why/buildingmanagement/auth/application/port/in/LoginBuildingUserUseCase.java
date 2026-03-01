package com.why.buildingmanagement.auth.application.port.in;

public interface LoginBuildingUserUseCase {
    String login(LoginBuildingUserCommand command);
}
