package com.why.buildingmanagement.auth.application.port.in;

public record LoginBuildingUserCommand(String usernameOrEmail, String password) {
}
