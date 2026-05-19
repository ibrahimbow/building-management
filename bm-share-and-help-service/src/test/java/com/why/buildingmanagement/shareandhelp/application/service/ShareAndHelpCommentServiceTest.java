package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.mapper.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.application.port.in.AddCommentCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteCommentCommand;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareAndHelpCommentServiceTest {

    @Mock
    private LoadShareAndHelpPostPort loadShareAndHelpPostPort;

    @Mock
    private SaveShareAndHelpPostPort saveShareAndHelpPostPort;

    @Mock
    private ShareAndHelpResultMapper shareAndHelpResultMapper;

    @InjectMocks
    private ShareAndHelpCommentService shareAndHelpCommentService;

    @Test
    void shouldAddCommentToPost() {

        final UUID postId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(postId);

        final ShareAndHelpPostResult result =
                createResult(postId, post.getBuildingId());

        final AddCommentCommand command = new AddCommentCommand(
                postId,
                1001L,
                "Tenant One",
                null,
                "I can help you with that.");

        when(loadShareAndHelpPostPort.loadById(postId))
                .thenReturn(Optional.of(post));

        when(saveShareAndHelpPostPort.save(post))
                .thenReturn(post);

        when(shareAndHelpResultMapper.toResult(post))
                .thenReturn(result);

        final ShareAndHelpPostResult actual =
                shareAndHelpCommentService.addComment(command);

        assertThat(actual).isEqualTo(result);
        assertThat(post.getComments()).hasSize(1);
        assertThat(post.getComments().getFirst().getComment())
                .isEqualTo("I can help you with that.");
        assertThat(post.getComments().getFirst().getCreatedByUserId())
                .isEqualTo(1001L);

        verify(loadShareAndHelpPostPort).loadById(postId);
        verify(saveShareAndHelpPostPort).save(post);
        verify(shareAndHelpResultMapper).toResult(post);
    }

    @Test
    void shouldThrowExceptionWhenAddingCommentToUnknownPost() {

        final UUID postId = UUID.randomUUID();

        final AddCommentCommand command = new AddCommentCommand(
                postId,
                1001L,
                "Tenant One",
                null,
                "I can help you with that.");

        when(loadShareAndHelpPostPort.loadById(postId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareAndHelpCommentService.addComment(command))
                .isInstanceOf(ShareAndHelpPostNotFoundException.class);

        verify(loadShareAndHelpPostPort).loadById(postId);
    }

    @Test
    void shouldDeleteOwnComment() {

        final UUID postId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(postId);

        post.addComment(ShareAndHelpComment.createNew(
                postId,
                1001L,
                "Tenant One",
                null,
                "This comment should be deleted."));

        final UUID commentId = post.getComments().getFirst().getId();

        final DeleteCommentCommand command = new DeleteCommentCommand(
                postId,
                commentId,
                1001L);

        when(loadShareAndHelpPostPort.loadById(postId))
                .thenReturn(Optional.of(post));

        when(saveShareAndHelpPostPort.save(post))
                .thenReturn(post);

        shareAndHelpCommentService.deleteComment(command);

        assertThat(post.getComments().getFirst().isDeleted()).isTrue();

        verify(loadShareAndHelpPostPort).loadById(postId);
        verify(saveShareAndHelpPostPort).save(post);
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownComment() {

        final UUID postId = UUID.randomUUID();

        final ShareAndHelpPost post = createPost(postId);

        final DeleteCommentCommand command = new DeleteCommentCommand(
                postId,
                UUID.randomUUID(),
                1001L);

        when(loadShareAndHelpPostPort.loadById(postId))
                .thenReturn(Optional.of(post));

        assertThatThrownBy(() -> shareAndHelpCommentService.deleteComment(command))
                .isInstanceOf(ShareAndHelpCommentNotFoundException.class);

        verify(loadShareAndHelpPostPort).loadById(postId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCommentFromUnknownPost() {

        final UUID postId = UUID.randomUUID();

        final DeleteCommentCommand command = new DeleteCommentCommand(
                postId,
                UUID.randomUUID(),
                1001L);

        when(loadShareAndHelpPostPort.loadById(postId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shareAndHelpCommentService.deleteComment(command))
                .isInstanceOf(ShareAndHelpPostNotFoundException.class);

        verify(loadShareAndHelpPostPort).loadById(postId);
    }

    private static ShareAndHelpPost createPost(final UUID postId) {

        return ShareAndHelpPost.restore(
                postId,
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

    private static ShareAndHelpPostResult createResult(final UUID postId,
                                                       final UUID buildingId) {

        return new ShareAndHelpPostResult(
                postId,
                buildingId,
                "Need a ladder",
                "Does anyone have a ladder I can borrow this weekend?",
                1001L,
                "Tenant One",
                null,
                Instant.parse("2026-05-14T10:00:00Z"),
                Instant.parse("2026-05-14T10:00:00Z"),
                null,
                List.of());
    }
}