package com.why.buildingmanagement.shareandhelp.application.service;

import com.why.buildingmanagement.shareandhelp.application.port.in.CreateShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.CreateShareAndHelpPostUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.DeleteShareAndHelpPostUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.GetShareAndHelpPostsUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.UpdateShareAndHelpPostCommand;
import com.why.buildingmanagement.shareandhelp.application.port.in.UpdateShareAndHelpPostUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.SaveShareAndHelpPostPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpResultMapper;
import com.why.buildingmanagement.shareandhelp.domain.exception.ShareAndHelpPostNotFoundException;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;
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
        DeleteShareAndHelpPostUseCase {

    private final LoadShareAndHelpPostPort loadShareAndHelpPostPort;
    private final SaveShareAndHelpPostPort saveShareAndHelpPostPort;
    private final ShareAndHelpResultMapper shareAndHelpResultMapper;

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
}