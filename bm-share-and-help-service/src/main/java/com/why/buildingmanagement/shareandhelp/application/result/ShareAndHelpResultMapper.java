package com.why.buildingmanagement.shareandhelp.application.result;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpComment;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import org.springframework.stereotype.Component;

@Component
public class ShareAndHelpResultMapper {

    public ShareAndHelpPostResult toResult(final ShareAndHelpPost post) {

        return new ShareAndHelpPostResult(
                post.getId(),
                post.getBuildingId(),
                post.getTitle(),
                post.getDescription(),
                post.getCreatedByUserId(),
                post.getCreatedByDisplayName(),
                post.getCreatedByAvatarUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getImageUrl(),
                post.getComments()
                        .stream()
                        .map(this::toResult)
                        .toList());
    }

    private ShareAndHelpCommentResult toResult(final ShareAndHelpComment comment) {

        return new ShareAndHelpCommentResult(
                comment.getId(),
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getCreatedByUserId(),
                comment.getCreatedByDisplayName(),
                comment.getCreatedByAvatarUrl());
    }
}