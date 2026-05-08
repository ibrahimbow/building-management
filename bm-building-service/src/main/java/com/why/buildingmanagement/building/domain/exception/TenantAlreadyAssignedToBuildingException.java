package com.why.buildingmanagement.building.domain.exception;

public class TenantAlreadyAssignedToBuildingException extends RuntimeException {
  public TenantAlreadyAssignedToBuildingException(String message) {
    super(message);
  }
}
