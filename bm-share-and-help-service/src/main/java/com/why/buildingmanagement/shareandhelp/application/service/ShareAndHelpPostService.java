package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.mapper.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.application.port.in.*;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.publisher.ShareAndHelpEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ShareAndHelpPostService implements
                CreateShareAndHelpPostUseCase,
                GetShareAndHelpPostsUseCase,
                UpdateShareAndHelpPostUseCase,
                DeleteShareAndHelpPostUseCase,
                AdminDeleteShareAndHelpPostUseCase,
                AdminGetShareAndHelpPostsUseCase {

    private final LoadShareAndHelpPostPort loadShareAndHelpPostPort;
    private final SaveShareAndHelpPostPort saveShareAndHelpPostPort;
    private final ShareAndHelpResultMapper shareAndHelpResultMapper;
    private final ShareAndHelpEventPublisher shareAndHelpEventPublisher;

    @Override
    public ShareAndHelpPostResult create(final CreateShareAndHelpPostCommand command) {

        final ShareAndHelpPost post = ShareAndHelpPost.createNew(
                        command.buildingId(),
                        command.createdByUserId(),
                        command.createdByDisplayName(),
                        command.createdByAvatarUrl(),
                        command.title(),
                        command.description(),
                        command.imageUrl());

        final ShareAndHelpPost savedPost = saveShareAndHelpPostPort.save(post);

        shareAndHelpEventPublisher.publishPostCreated(
                        new ShareAndHelpPostCreatedEvent(
                                        savedPost.getId(),
                                        savedPost.getBuildingId(),
                                        savedPost.getCreatedByUserId(),
                                        savedPost.getTitle(),
                                        savedPost.getCreatedByDisplayName(),
                                        savedPost.getCreatedAt()));

        return shareAndHelpResultMapper.toResult(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareAndHelpPostResult> getByBuildingId(final UUID buildingId) {

        return loadShareAndHelpPostPort.loadByBuildingId(buildingId)
                        .stream()
                        .map(shareAndHelpResultMapper::toResult)
                        .toList();
    }

    @Override
    public ShareAndHelpPostResult update(final UpdateShareAndHelpPostCommand command) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort
                        .loadByIdAndCreatedByUserId(command.postId(), command.userId())
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(command.postId()));

        post.update(
                        command.title(),
                        command.description(),
                        command.imageUrl());

        final ShareAndHelpPost savedPost = saveShareAndHelpPostPort.save(post);

        return shareAndHelpResultMapper.toResult(savedPost);
    }

    @Override
    public void delete(final DeleteShareAndHelpPostCommand command) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort
                        .loadByIdAndCreatedByUserId(command.postId(), command.userId())
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(command.postId()));

        post.delete();

        saveShareAndHelpPostPort.save(post);
    }

    @Override
    public void deletePostByAdmin(final UUID postId) {

        final ShareAndHelpPost post = loadShareAndHelpPostPort
                        .loadById(postId)
                        .orElseThrow(() -> new ShareAndHelpPostNotFoundException(postId));

        post.delete();

        saveShareAndHelpPostPort.save(post);
    }

    @Override
    public List<ShareAndHelpPostResult> getAllPostsForAdmin() {

        return loadShareAndHelpPostPort.loadAll()
                        .stream()
                        .map(shareAndHelpResultMapper::toResult)
                        .toList();
    }
}