package com.why.buildingmanagement.auth.infrastructure.api.error;

import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateUsernameException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateUsername(
            DuplicateUsernameException ex,
            HttpServletRequest request) {
        return error(
                HttpStatus.CONFLICT,
                "DUPLICATE_USERNAME",
                ex.getMessage(),
                request.getRequestURI(),
                null);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateEmail(
            DuplicateEmailException ex,
            HttpServletRequest request) {
        return error(
                HttpStatus.CONFLICT,
                "DUPLICATE_EMAIL",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {
        return error(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid username/email or password",
                request.getRequestURI(),
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Request validation failed",
                request.getRequestURI(),
                validationErrors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return error(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                request.getRequestURI(),
                null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error",
                request.getRequestURI(),
                null);
    }

    private ApiErrorResponse error(
            HttpStatus status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path,
                validationErrors
        );
    }
}