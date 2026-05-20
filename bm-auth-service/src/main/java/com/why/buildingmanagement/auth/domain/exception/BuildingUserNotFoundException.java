package com.why.buildingmanagement.auth.domain.exception;

public class BuildingUserNotFoundException extends RuntimeException {
  public BuildingUserNotFoundException(String message) {
    super(message);
  }
}
