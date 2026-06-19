package com.why.buildingmanagement.auth.infrastructure.api.exception;

import com.why.buildingmanagement.auth.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateUsername(final DuplicateUsernameException ex, final HttpServletRequest request) {
        return error(HttpStatus.CONFLICT,
                     "DUPLICATE_USERNAME",
                     ex.getMessage(),
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleDuplicateEmail(final DuplicateEmailException ex, final HttpServletRequest request) {
        return error(HttpStatus.CONFLICT,
                     "DUPLICATE_EMAIL",
                     ex.getMessage(),
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidCredentials(final InvalidCredentialsException ex, final HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED,
                     "INVALID_CREDENTIALS",
                     "Invalid username/email or password",
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidRefreshToken(final InvalidRefreshTokenException ex, final HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED,
                     "INVALID_REFRESH_TOKEN",
                     "Invalid or expired refresh token",
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(InvalidBuildingUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidBuildingUser(final InvalidBuildingUserException ex, final HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST,
                     "INVALID_BUILDING_USER",
                     ex.getMessage(),
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        final Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(fieldError -> validationErrors.put(
                          fieldError.getField(),
                          fieldError.getDefaultMessage()));

        return error(HttpStatus.BAD_REQUEST,
                     "VALIDATION_FAILED",
                     "Request validation failed",
                     request.getRequestURI(),
                     validationErrors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleAccessDenied(final AccessDeniedException ex, final HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN,
                     "ACCESS_DENIED",
                     "You do not have permission to access this resource",
                     request.getRequestURI(),
                     null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleGenericException(final Exception ex, final HttpServletRequest request) {
        log.error("Unexpected error occurred for path {}",
                  request.getRequestURI(),
                  ex);

        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                     "INTERNAL_SERVER_ERROR",
                     "An unexpected error occurred",
                     request.getRequestURI(),
                     null);
    }

    private ApiErrorResponse error(final HttpStatus status,
                                   final String error,
                                   final String message,
                                   final String path,
                                   final Map<String, String> validationErrors) {
        return new ApiErrorResponse(Instant.now(),
                                    status.value(),
                                    error,
                                    message,
                                    path,
                                    validationErrors);
    }
}