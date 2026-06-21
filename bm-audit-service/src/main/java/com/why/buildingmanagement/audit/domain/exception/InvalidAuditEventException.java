package com.why.buildingmanagement.audit.domain.exception;

public class InvalidAuditEventException extends RuntimeException {
    public InvalidAuditEventException(String message) {
        super(message);
    }
}
