package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.mapper.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.application.port.in.CreateShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.ResolveShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.UpdateShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPostStatus;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.publisher.ShareAndHelpEventPublisher;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareAndHelpPostServiceTest {

    @Mock
    private LoadShareAndHelpPostPort loadShareAndHelpPostPort;

    @Mock
    private SaveShareAndHelpPostPort saveShareAndHelpPostPort;

    @Mock
    private ShareAndHelpResultMapper shareAndHelpResultMapper;

    @InjectMocks
    private ShareAndHelpPostService shareAndHelpPostService;

    @Mock
    private ShareAndHelpEventPublisher shareAndHelpEventPublisher;


    @Test
    void shouldCreateShareAndHelpPost() {

        final UUID buildingId = UUID.randomUUID();

        final CreateShareAndHelpPostCommand command =
                        new CreateShareAndHelpPostCommand(
                                        buildingId,
                                        1001L,
                                        "Tenant One",
                                        null,
                                        "Need a ladder",
                                        "Does anyone have a ladder I can borrow this weekend?",
                                        null);

        final ShareAndHelpPost savedPost = createPost(buildingId);

        final ShareAndHelpPostResult result = createResult(savedPost.getId(), savedPost.getBuildingId());

        when(saveShareAndHelpPostPort.save(any(ShareAndHelpPost.class)))
                        .thenReturn(savedPost);

        when(shareAndHelpResultMapper.toResult(savedPost))
                        .thenReturn(result);

        final ShareAndHelpPostResult actual = shareAndHelpPostService.create(command);

        assertThat(actual).isEqualTo(result);

        verify(shareAndHelpEventPublisher).publishPostCreated(any());

        verify(saveShareAndHelpPostPort)
                        .save(any(ShareAndHelpPost.class));

        verify(shareAndHelpResultMapper).toResult(savedPost);
    }

    @Test
    void shouldGetPostsByBuildingId() {

        final UUID buildingId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(buildingId);

        final ShareAndHelpPostResult result = createResult(post.getId(), post.getBuildingId());

        when(loadShareAndHelpPostPort.loadByBuildingId(buildingId))
                        .thenReturn(List.of(post));

        when(shareAndHelpResultMapper.toResult(post))
                        .thenReturn(result);

        final List<ShareAndHelpPostResult> actual =
                        shareAndHelpPostService.getByBuildingId(buildingId);

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst()).isEqualTo(result);

        verify(loadShareAndHelpPostPort).loadByBuildingId(buildingId);
        verify(shareAndHelpResultMapper).toResult(post);
    }

    @Test
    void shouldUpdateShareAndHelpPost() {

        final UUID postId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(buildingId);

        final UpdateShareAndHelpPostCommand command =
                        new UpdateShareAndHelpPostCommand(
                                        postId,
                                        1001L,
                                        "Updated title",
                                        "Updated description",
                                        null);

        final ShareAndHelpPostResult result = createResult(post.getId(), post.getBuildingId());

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.of(post));

        when(saveShareAndHelpPostPort.save(post))
                        .thenReturn(post);

        when(shareAndHelpResultMapper.toResult(post))
                        .thenReturn(result);

        final ShareAndHelpPostResult actual =
                        shareAndHelpPostService.update(command);

        assertThat(actual).isEqualTo(result);
        assertThat(post.getTitle()).isEqualTo("Updated title");
        assertThat(post.getDescription()).isEqualTo("Updated description");

        verify(loadShareAndHelpPostPort)
                        .loadByIdAndCreatedByUserId(postId, 1001L);

        verify(saveShareAndHelpPostPort).save(post);
        verify(shareAndHelpResultMapper).toResult(post);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownPost() {

        final UUID postId = UUID.randomUUID();

        final UpdateShareAndHelpPostCommand command =
                        new UpdateShareAndHelpPostCommand(
                                        postId,
                                        1001L,
                                        "Updated title",
                                        "Updated description",
                                        null);

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareAndHelpPostService.update(command))
                        .isInstanceOf(ShareAndHelpPostNotFoundException.class);

        verify(loadShareAndHelpPostPort)
                        .loadByIdAndCreatedByUserId(postId, 1001L);
    }

    @Test
    void shouldDeleteShareAndHelpPost() {

        final UUID postId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(buildingId);

        final DeleteShareAndHelpPostCommand command =
                        new DeleteShareAndHelpPostCommand(
                                        postId,
                                        1001L);

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.of(post));

        when(saveShareAndHelpPostPort.save(post))
                        .thenReturn(post);

        shareAndHelpPostService.delete(command);

        assertThat(post.isDeleted()).isTrue();

        verify(loadShareAndHelpPostPort)
                        .loadByIdAndCreatedByUserId(postId, 1001L);

        verify(saveShareAndHelpPostPort).save(post);
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownPost() {

        final UUID postId = UUID.randomUUID();

        final DeleteShareAndHelpPostCommand command =
                        new DeleteShareAndHelpPostCommand(
                                        postId,
                                        1001L);

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareAndHelpPostService.delete(command))
                        .isInstanceOf(ShareAndHelpPostNotFoundException.class);

        verify(loadShareAndHelpPostPort)
                        .loadByIdAndCreatedByUserId(postId, 1001L);
    }

    @Test
    void shouldResolveShareAndHelpPost() {

        final UUID postId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(buildingId);

        final ResolveShareAndHelpPostCommand command = new ResolveShareAndHelpPostCommand(postId,
                                                                                          buildingId,
                                                                                          1001L);

        final ShareAndHelpPostResult result = createResult(post.getId(), post.getBuildingId());

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.of(post));

        when(saveShareAndHelpPostPort.save(post))
                        .thenReturn(post);

        when(shareAndHelpResultMapper.toResult(post))
                        .thenReturn(result);

        final ShareAndHelpPostResult actual = shareAndHelpPostService.resolvePost(command);

        assertThat(actual).isEqualTo(result);
        assertThat(post.isResolved()).isTrue();

        verify(loadShareAndHelpPostPort)
                        .loadByIdAndCreatedByUserId(postId, 1001L);

        verify(saveShareAndHelpPostPort).save(post);
        verify(shareAndHelpResultMapper).toResult(post);
    }

    @Test
    void shouldThrowExceptionWhenResolvingUnknownPost() {

        final UUID postId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        final ResolveShareAndHelpPostCommand command = new ResolveShareAndHelpPostCommand(postId,
                                                                                          buildingId,
                                                                                          1001L);

        when(loadShareAndHelpPostPort.loadByIdAndCreatedByUserId(postId, 1001L))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareAndHelpPostService.resolvePost(command))
                        .isInstanceOf(ShareAndHelpPostNotFoundException.class);

        verify(loadShareAndHelpPostPort).loadByIdAndCreatedByUserId(postId, 1001L);
    }

    private static ShareAndHelpPost createPost(final UUID buildingId) {

        return ShareAndHelpPost.restore(UUID.randomUUID(),
                                        buildingId,
                                        1001L,
                                        "Tenant One",
                                        null,
                                        "Need a ladder",
                                        "Does anyone have a ladder I can borrow this weekend?",
                                        null,
                                        ShareAndHelpPostStatus.OPEN,
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        null,
                                        List.of());
    }

    private static ShareAndHelpPostResult createResult(final UUID postId,
                                                       final UUID buildingId) {

        return new ShareAndHelpPostResult(postId,
                                          buildingId,
                                          "Need a ladder",
                                          "Does anyone have a ladder I can borrow this weekend?",
                                          1001L,
                                          "Tenant One",
                                          null,
                                          Instant.parse("2026-05-14T10:00:00Z"),
                                          Instant.parse("2026-05-14T10:00:00Z"),
                                          null,
                                          ShareAndHelpPostStatus.OPEN,
                                          List.of());
    }
}