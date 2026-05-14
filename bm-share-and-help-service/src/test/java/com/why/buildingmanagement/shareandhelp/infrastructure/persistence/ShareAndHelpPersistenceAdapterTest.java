package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareAndHelpPersistenceAdapterTest {

    @Mock
    private ShareAndHelpRepository repository;

    @Mock
    private ShareAndHelpPersistenceMapper mapper;

    @InjectMocks
    private ShareAndHelpPersistenceAdapter adapter;

    @Test
    void shouldSaveShareAndHelpPost() {

        final ShareAndHelpPost post = createPost();
        final ShareAndHelpPostEntity entity = createEntity(post);
        final ShareAndHelpPostEntity savedEntity = createEntity(post);
        final ShareAndHelpPost savedPost = createPost();

        when(mapper.toEntity(post)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedPost);

        final ShareAndHelpPost result = adapter.save(post);

        assertThat(result).isEqualTo(savedPost);

        verify(mapper).toEntity(post);
        verify(repository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void shouldLoadByBuildingId() {

        final UUID buildingId = UUID.randomUUID();
        final ShareAndHelpPost post = createPost();
        final ShareAndHelpPostEntity entity = createEntity(post);

        when(repository.findAllByBuildingIdAndDeletedAtIsNullOrderByCreatedAtDesc(buildingId))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(post);

        final List<ShareAndHelpPost> result = adapter.loadByBuildingId(buildingId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(post);

        verify(repository).findAllByBuildingIdAndDeletedAtIsNullOrderByCreatedAtDesc(buildingId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldLoadById() {

        final UUID postId = UUID.randomUUID();
        final ShareAndHelpPost post = createPost();
        final ShareAndHelpPostEntity entity = createEntity(post);

        when(repository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(post);

        final Optional<ShareAndHelpPost> result = adapter.loadById(postId);

        assertThat(result).isPresent();

        assertThat(result).contains(post);


        verify(repository).findByIdAndDeletedAtIsNull(postId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenPostNotFoundById() {

        final UUID postId = UUID.randomUUID();

        when(repository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.empty());

        final Optional<ShareAndHelpPost> result = adapter.loadById(postId);

        assertThat(result).isEmpty();

        verify(repository).findByIdAndDeletedAtIsNull(postId);
    }

    @Test
    void shouldLoadByIdAndCreatedByUserId() {

        final UUID postId = UUID.randomUUID();
        final Long createdByUserId = 1001L;
        final ShareAndHelpPost post = createPost();
        final ShareAndHelpPostEntity entity = createEntity(post);

        when(repository.findByIdAndCreatedByUserIdAndDeletedAtIsNull(postId, createdByUserId))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(post);

        final Optional<ShareAndHelpPost> result =
                adapter.loadByIdAndCreatedByUserId(postId, createdByUserId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(post);

        verify(repository).findByIdAndCreatedByUserIdAndDeletedAtIsNull(postId, createdByUserId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenPostNotFoundByIdAndCreatedByUserId() {

        final UUID postId = UUID.randomUUID();
        final Long createdByUserId = 1001L;

        when(repository.findByIdAndCreatedByUserIdAndDeletedAtIsNull(postId, createdByUserId))
                .thenReturn(Optional.empty());

        final Optional<ShareAndHelpPost> result =
                adapter.loadByIdAndCreatedByUserId(postId, createdByUserId);

        assertThat(result).isEmpty();

        verify(repository).findByIdAndCreatedByUserIdAndDeletedAtIsNull(postId, createdByUserId);
    }

    private static ShareAndHelpPost createPost() {

        return ShareAndHelpPost.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1001L,
                "Tenant One",
                null,
                "Need a ladder",
                "Does anyone have a ladder I can borrow this weekend?",
                null,
                Instant.parse("2026-05-14T10:00:00Z"),
                Instant.parse("2026-05-14T10:00:00Z"),
                null,
                List.of());
    }

    private static ShareAndHelpPostEntity createEntity(final ShareAndHelpPost post) {

        return new ShareAndHelpPostEntity(
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
                new java.util.ArrayList<>());
    }
}