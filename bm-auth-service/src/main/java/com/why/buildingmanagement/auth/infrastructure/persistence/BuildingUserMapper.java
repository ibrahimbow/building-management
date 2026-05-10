package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingUserMapper {

    default BuildingUserEntity toEntity(final BuildingUser user) {

        return BuildingUserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    default BuildingUser toDomain(final BuildingUserEntity entity) {

        return new BuildingUser(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getNickname(),
                entity.getPhoneNumber(),
                BuildingUserRole.valueOf(entity.getRole()),
                entity.getCreatedAt(),
                entity.isEnabled()
        );
    }
}