package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingUserMapper {

    default BuildingUserEntity toEntity(final BuildingUser user) {

        return new BuildingUserEntity(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getDisplayName(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt());
    }

    default BuildingUser toDomain(final BuildingUserEntity entity) {

        return new BuildingUser(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getDisplayName(),
                entity.getPhoneNumber(),
                entity.getAvatarUrl(),
                BuildingUserRole.valueOf(entity.getRole()),
                entity.getCreatedAt(),
                entity.isEnabled());
    }
}