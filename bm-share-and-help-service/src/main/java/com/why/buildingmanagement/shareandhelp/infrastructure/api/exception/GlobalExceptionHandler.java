package com.why.buildingmanagement.shareandhelp.infrastructure.api.exception;

import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentAlreadyDeletedException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostDeletedException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShareAndHelpPostNotFoundException.class)
    public ProblemDetail handleShareAndHelpPostNotFoundException(
            final ShareAndHelpPostNotFoundException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("Share and Help post not found");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(ShareAndHelpCommentNotFoundException.class)
    public ProblemDetail handleShareAndHelpCommentNotFoundException(
            final ShareAndHelpCommentNotFoundException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("Share and Help comment not found");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler({
            ShareAndHelpPostDeletedException.class,
            ShareAndHelpCommentAlreadyDeletedException.class
    })
    public ProblemDetail handleConflictExceptions(final RuntimeException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Share and Help conflict");
        problemDetail.setDetail(exception.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            final MethodArgumentNotValidException exception) {

        final String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed.");

        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail(errorMessage);

        return problemDetail;
    }
}