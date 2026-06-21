package com.why.buildingmanagement.auth.infrastructure.kafka.event;

public enum AuditEventType {

    USER_LOGIN_SUCCESS,
    USER_LOGIN_FAILED,
    USER_REGISTERED,
    PASSWORD_CHANGED,
    PROFILE_UPDATED
}