package com.why.buildingmanagement.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(
        name = "building_users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_building_users_username",
                        columnNames = "username"),
                @UniqueConstraint(
                        name = "uk_building_users_email",
                        columnNames = "email")
        },
        indexes = {
                @Index(
                        name = "idx_building_users_username",
                        columnList = "username"),
                @Index(
                        name = "idx_building_users_email",
                        columnList = "email")
        }
)
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 150)
    private String displayName;

    @Column(name = "phone_number", nullable = false, length = 30)
    private String phoneNumber;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}