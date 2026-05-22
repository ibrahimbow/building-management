package com.why.buildingmanagement.auth.domain.exception;

public class BuildingUserNotFoundException extends RuntimeException {

    public BuildingUserNotFoundException(final Long userId) {

        super("Building user not found with id: " + userId);
    }
}