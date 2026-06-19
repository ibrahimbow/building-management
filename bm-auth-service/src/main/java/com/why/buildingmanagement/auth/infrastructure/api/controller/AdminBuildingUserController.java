package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.DisableBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.GetAllBuildingUsersUseCase;
import com.why.buildingmanagement.auth.application.result.AdminBuildingUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminBuildingUserController {

    private final GetAllBuildingUsersUseCase getAllBuildingUsersUseCase;
    private final DisableBuildingUserUseCase disableBuildingUserUseCase;

    @GetMapping
    public ResponseEntity<List<AdminBuildingUserResult>> getAllUsers() {

        return ResponseEntity.ok(getAllBuildingUsersUseCase.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> disableUser(@PathVariable("userId") final Long userId) {

        disableBuildingUserUseCase.disableUser(userId);

        return ResponseEntity.noContent().build();
    }
}