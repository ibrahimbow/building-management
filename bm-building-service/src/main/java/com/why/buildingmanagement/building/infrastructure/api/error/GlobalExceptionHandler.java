package com.why.buildingmanagement.building.infrastructure.api.error;

import com.why.buildingmanagement.building.domain.exception.BuildingNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BuildingNotFoundException.class)
    public ResponseEntity<String> handleBuildingNotFound(BuildingNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(ex.getMessage());
    }
}