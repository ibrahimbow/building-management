package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.port.in.AddCommentCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.AddCommentUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteCommentCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteCommentUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpCommentNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShareAndHelpCommentService implements AddCommentUseCase, DeleteCommentUseCase {

    private final LoadShareAndHelpPostPort loadShareAndHelpPostPort;
    private final SaveShareAndHelpPostPort saveShareAndHelpPostPort;
    private final ShareAndHelpResultMapper shareAndHelpResultMapper;

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
}