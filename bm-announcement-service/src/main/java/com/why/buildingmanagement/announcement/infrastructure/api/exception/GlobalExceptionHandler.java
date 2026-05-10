package com.why.buildingmanagement.announcement.infrastructure.api.exception;

import com.why.buildingmanagement.announcement.domain.exception.AnnouncementNotFoundException;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementOwnershipException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AnnouncementNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAnnouncementNotFound(
            final AnnouncementNotFoundException exception) {

        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Announcement not found");
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(AnnouncementOwnershipException.class)
    public ResponseEntity<ProblemDetail> handleAnnouncementOwnership(
            final AnnouncementOwnershipException exception) {

        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Access denied");
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAuthorizationDenied(
            final AuthorizationDeniedException exception) {

        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Access denied");
        problem.setDetail("You do not have permission to access this resource");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            final MethodArgumentNotValidException exception) {

        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        final String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request");

        problem.setTitle("Validation failed");
        problem.setDetail(message);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            final Exception exception) {

        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal server error");
        problem.setDetail("Unexpected error occurred");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}