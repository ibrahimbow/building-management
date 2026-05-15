package com.why.buildingmanagement.file.infrastructure.exception;

import com.why.buildingmanagement.file.domain.exception.InvalidFileException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidFile(final InvalidFileException exception) {
        return Map.of("message", exception.getMessage());
    }
}