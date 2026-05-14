package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShareAndHelpPersistenceMapper {

    default ShareAndHelpPost toDomain(final ShareAndHelpPostEntity entity) {

        final List<ShareAndHelpComment> comments = entity.getComments()
                .stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .map(this::toDomain)
                .toList();

        return ShareAndHelpPost.restore(
                entity.getId(),
                entity.getBuildingId(),
                entity.getCreatedByUserId(),
                entity.getCreatedByDisplayName(),
                entity.getCreatedByAvatarUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                comments);
    }

    default ShareAndHelpComment toDomain(final ShareAndHelpCommentEntity entity) {

        return ShareAndHelpComment.restore(
                entity.getId(),
                entity.getPost().getId(),
                entity.getCreatedByUserId(),
                entity.getCreatedByDisplayName(),
                entity.getCreatedByAvatarUrl(),
                entity.getComment(),
                entity.getCreatedAt(),
                entity.getDeletedAt());
    }

    default ShareAndHelpPostEntity toEntity(final ShareAndHelpPost post) {

        final ShareAndHelpPostEntity entity = new ShareAndHelpPostEntity(
                post.getId(),
                post.getBuildingId(),
                post.getCreatedByUserId(),
                post.getCreatedByDisplayName(),
                post.getCreatedByAvatarUrl(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getDeletedAt(),
                new ArrayList<>());

        final List<ShareAndHelpCommentEntity> comments = post.getComments()
                .stream()
                .map(comment -> toEntity(comment, entity))
                .toList();

        entity.replaceComments(comments);

        return entity;
    }

    default ShareAndHelpCommentEntity toEntity(final ShareAndHelpComment comment,
                                               final ShareAndHelpPostEntity post) {

        return new ShareAndHelpCommentEntity(
                comment.getId(),
                post,
                comment.getComment(),
                comment.getCreatedByUserId(),
                comment.getCreatedByDisplayName(),
                comment.getCreatedByAvatarUrl(),
                comment.getCreatedAt(),
                comment.getDeletedAt());
    }
}