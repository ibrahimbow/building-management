package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.mapper.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.application.port.in.*;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.publisher.ShareAndHelpEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ShareAndHelpCommentService implements AddCommentUseCase, DeleteCommentUseCase, AdminDeleteCommentUseCase {

    private final LoadShareAndHelpPostPort loadShareAndHelpPostPort;
    private final SaveShareAndHelpPostPort saveShareAndHelpPostPort;
    private final ShareAndHelpResultMapper shareAndHelpResultMapper;
    private final ShareAndHelpEventPublisher shareAndHelpEventPublisher;


    @Override
    public ShareAndHelpPostResult addComment(final AddCommentCommand command) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort.loadById(command.postId())
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(command.postId()));

        final ShareAndHelpComment comment = ShareAndHelpComment.createNew(
                        command.postId(),
                        command.createdByUserId(),
                        command.createdByDisplayName(),
                        command.createdByAvatarUrl(),
                        command.comment());

        post.addComment(comment);

        final ShareAndHelpPost savedPost = saveShareAndHelpPostPort.save(post);

        shareAndHelpEventPublisher.publishCommentCreated(
                        new ShareAndHelpCommentCreatedEvent(
                                        comment.getId(),
                                        savedPost.getId(),
                                        savedPost.getBuildingId(),
                                        savedPost.getCreatedByUserId(),
                                        command.createdByUserId(),
                                        savedPost.getTitle(),
                                        command.createdByDisplayName(),
                                        comment.getCreatedAt()));

        return shareAndHelpResultMapper.toResult(savedPost);
    }

    @Override
    public void deleteComment(final DeleteCommentCommand command) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort.loadById(command.postId())
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(command.postId()));

        final ShareAndHelpComment comment = post.getComments()
                        .stream()
                        .filter(existingComment -> existingComment.getId().equals(command.commentId()))
                        .filter(existingComment -> existingComment.isOwnedBy(command.userId()))
                        .findFirst()
                        .orElseThrow(() -> new ShareAndHelpCommentNotFoundException(command.commentId()));

        comment.delete();

        saveShareAndHelpPostPort.save(post);
    }

    @Override
    public void deleteCommentByAdmin(final UUID postId, final UUID commentId) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort.loadById(postId)
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(postId));

        post.deleteComment(commentId);

        saveShareAndHelpPostPort.save(post);
    }
}