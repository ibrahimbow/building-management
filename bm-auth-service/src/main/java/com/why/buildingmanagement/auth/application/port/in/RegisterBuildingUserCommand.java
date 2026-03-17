package com.why.buildingmanagement.auth.application.port.in;

public record RegisterBuildingUserCommand(String username, String email, String password, String role) {
}
