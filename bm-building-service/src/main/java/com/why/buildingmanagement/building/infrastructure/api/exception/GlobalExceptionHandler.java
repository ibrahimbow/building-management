package com.why.buildingmanagement.building.infrastructure.api.exception;

import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import com.why.buildingmanagement.building.domain.exception.TenantAlreadyJoinedBuildingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BuildingNotFoundException.class)
    public ProblemDetail handleBuildingNotFound(
            final BuildingNotFoundException exception,
            final HttpServletRequest request
    ) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        enrich(problem, request, "Building not found");

        return problem;
    }

    @ExceptionHandler(TenantAlreadyJoinedBuildingException.class)
    public ProblemDetail handleTenantAlreadyJoined(
            final TenantAlreadyJoinedBuildingException exception,
            final HttpServletRequest request
    ) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        enrich(problem, request, "Tenant already joined building");

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request
    ) {
        final String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                message
        );

        enrich(problem, request, "Validation failed");

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            final IllegalArgumentException exception,
            final HttpServletRequest request
    ) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );

        enrich(problem, request, "Bad request");

        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            final AccessDeniedException exception,
            final HttpServletRequest request
    ) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource"
        );

        enrich(problem, request, "Access denied");

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(
            final Exception exception,
            final HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", exception);

        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );

        enrich(problem, request, "Internal server error");

        return problem;
    }

    private void enrich(
            final ProblemDetail problem,
            final HttpServletRequest request,
            final String title
    ) {
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
    }
}