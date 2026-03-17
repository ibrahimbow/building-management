package com.why.buildingmanagement.auth.application.port.in;

public interface RegisterBuildingUserUseCase {
    Long register(RegisterBuildingUserCommand command);
}
