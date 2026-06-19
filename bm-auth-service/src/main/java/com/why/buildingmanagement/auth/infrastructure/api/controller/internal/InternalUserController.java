package com.why.buildingmanagement.auth.infrastructure.api.controller.internal;

import com.why.buildingmanagement.auth.infrastructure.api.dto.response.InternalUserResponse;
import com.why.buildingmanagement.auth.infrastructure.persistence.BuildingUserEntity;
import com.why.buildingmanagement.auth.infrastructure.persistence.BuildingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final BuildingUserRepository buildingUserJpaRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<InternalUserResponse> getUserById(@PathVariable("userId") final Long userId) {

        return buildingUserJpaRepository.findById(userId)
                                        .map(this::toResponse)
                                        .map(ResponseEntity::ok)
                                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private InternalUserResponse toResponse(final BuildingUserEntity user) {
        return new InternalUserResponse(user.getId(),
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        null);
    }
}