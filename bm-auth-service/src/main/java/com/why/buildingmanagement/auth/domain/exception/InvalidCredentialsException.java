package com.why.buildingmanagement.auth.domain.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("Username/email or password is incorrect");
    }
}
