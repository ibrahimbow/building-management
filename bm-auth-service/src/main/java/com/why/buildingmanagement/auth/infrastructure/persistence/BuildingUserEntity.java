package com.why.buildingmanagement.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "building_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_building_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_building_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_building_users_username", columnList = "username"),
                @Index(name = "idx_building_users_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BuildingUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false)
    @Builder.Default
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