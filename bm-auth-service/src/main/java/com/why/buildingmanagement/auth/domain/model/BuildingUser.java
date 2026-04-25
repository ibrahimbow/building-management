package com.why.buildingmanagement.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class BuildingUser {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private BuildingUserRole role;
    private Instant createdAt;
    private boolean enabled;

    public BuildingUser(Long id, String username, String email, String passwordHash, BuildingUserRole role, Instant createdAt, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.enabled = enabled;
    }
}
