package com.why.buildingmanagement.auth.domain.model;

import java.time.Instant;

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


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public BuildingUserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
