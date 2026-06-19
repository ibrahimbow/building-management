package com.why.buildingmanagement.chat.infrastructure.exception;

import com.why.buildingmanagement.chat.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatMessageNotFoundException.class)
    public ProblemDetail handleChatMessageNotFoundException(final ChatMessageNotFoundException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage());

        problemDetail.setTitle("Chat message not found");

        return problemDetail;
    }

    @ExceptionHandler(ChatMessageAccessDeniedException.class)
    public ProblemDetail handleChatMessageAccessDeniedException(final ChatMessageAccessDeniedException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        exception.getMessage());

        problemDetail.setTitle("Chat message access denied");

        return problemDetail;
    }

    @ExceptionHandler(EmptyChatMessageException.class)
    public ProblemDetail handleEmptyChatMessageException(final EmptyChatMessageException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage());

        problemDetail.setTitle("Invalid chat message");

        return problemDetail;
    }

    @ExceptionHandler(ChatMessageImageLimitExceededException.class)
    public ProblemDetail handleChatMessageImageLimitExceededException(final ChatMessageImageLimitExceededException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage());

        problemDetail.setTitle("Invalid chat message image");

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {

        final String message = exception.getBindingResult().getFieldErrors()
                                        .stream()
                                        .findFirst()
                                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                        .orElse("Validation failed");

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        message);

        problemDetail.setTitle("Validation failed");

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(final ConstraintViolationException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage());

        problemDetail.setTitle("Constraint violation");

        return problemDetail;
    }

    @ExceptionHandler(ManagerBuildingNotFoundException.class)
    public ProblemDetail handleManagerBuildingNotFoundException(final ManagerBuildingNotFoundException exception) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage());

        problemDetail.setTitle("Manager building not found");

        return problemDetail;
    }
}