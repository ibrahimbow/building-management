package com.why.buildingmanagement.shareandhelp.infrastructure.api.controller;

import com.why.buildingmanagement.shareandhelp.application.port.in.*;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadManagerBuildingPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPostStatus;
import com.why.buildingmanagement.shareandhelp.domain.model.UserRole;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request.AddShareAndHelpCommentRequest;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request.CreateShareAndHelpPostRequest;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request.UpdateShareAndHelpPostRequest;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.ShareAndHelpPostResponse;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.mapper.ShareAndHelpApiMapper;
import com.why.buildingmanagement.shareandhelp.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.shareandhelp.infrastructure.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenant/share-and-help/posts")
@RequiredArgsConstructor
public class ShareAndHelpController {

    private final CreateShareAndHelpPostUseCase createShareAndHelpPostUseCase;
    private final GetShareAndHelpPostsUseCase getShareAndHelpPostsUseCase;
    private final UpdateShareAndHelpPostUseCase updateShareAndHelpPostUseCase;
    private final DeleteShareAndHelpPostUseCase deleteShareAndHelpPostUseCase;
    private final AddCommentUseCase addCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final CurrentUserService currentUserService;
    private final ShareAndHelpApiMapper shareAndHelpApiMapper;
    private final LoadTenantBuildingPort loadTenantBuildingPort;
    private final LoadManagerBuildingPort loadManagerBuildingPort;
    private final UpdateShareAndHelpPostStatusUseCase updateShareAndHelpPostStatusUseCase;

    @PostMapping
    public ResponseEntity<ShareAndHelpPostResponse> create(@Valid @RequestBody final CreateShareAndHelpPostRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();
        final UUID buildingId = resolveBuildingIdForCurrentUser(currentUser);

        final var result = createShareAndHelpPostUseCase.create(
                        new CreateShareAndHelpPostCommand(buildingId,
                                                          currentUser.userId(),
                                                          currentUser.displayName(),
                                                          currentUser.avatarUrl(),
                                                          request.title(),
                                                          request.description(),
                                                          request.imageUrl()));

        return ResponseEntity.status(HttpStatus.CREATED).body(shareAndHelpApiMapper.toResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<ShareAndHelpPostResponse>> getPosts() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();
        final UUID buildingId = resolveBuildingIdForCurrentUser(currentUser);

        final List<ShareAndHelpPostResponse> response = getShareAndHelpPostsUseCase.getByBuildingId(buildingId)
                                                                                   .stream()
                                                                                   .map(shareAndHelpApiMapper::toResponse)
                                                                                   .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ShareAndHelpPostResponse> update(@PathVariable("postId") final UUID postId,
                                                           @Valid @RequestBody final UpdateShareAndHelpPostRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final var result = updateShareAndHelpPostUseCase.update(
                        new UpdateShareAndHelpPostCommand(postId,
                                                          currentUser.userId(),
                                                          request.title(),
                                                          request.description(),
                                                          request.imageUrl()));

        return ResponseEntity.ok(shareAndHelpApiMapper.toResponse(result));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable("postId") final UUID postId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        deleteShareAndHelpPostUseCase.delete(new DeleteShareAndHelpPostCommand(postId, currentUser.userId()));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ShareAndHelpPostResponse> addComment(@PathVariable("postId") final UUID postId,
                                                               @Valid @RequestBody final AddShareAndHelpCommentRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final var result = addCommentUseCase.addComment(new AddCommentCommand(postId,
                                                                              currentUser.userId(),
                                                                              currentUser.displayName(),
                                                                              currentUser.avatarUrl(),
                                                                              request.comment()));

        return ResponseEntity.status(HttpStatus.CREATED).body(shareAndHelpApiMapper.toResponse(result));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("postId") final UUID postId,
                                              @PathVariable("commentId") final UUID commentId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        deleteCommentUseCase.deleteComment(
                        new DeleteCommentCommand(
                                        postId,
                                        commentId,
                                        currentUser.userId()));

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{postId}/resolve")
    public ResponseEntity<ShareAndHelpPostResponse> resolvePost(
                    @PathVariable("postId") final UUID postId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();
        final UUID buildingId = resolveBuildingIdForCurrentUser(currentUser);

        final ShareAndHelpPostResult result =
                        updateShareAndHelpPostStatusUseCase.updatePostStatus(
                                        new UpdateShareAndHelpPostStatusCommand(postId,
                                                                                buildingId,
                                                                                currentUser.userId(),
                                                                                ShareAndHelpPostStatus.RESOLVED));

        return ResponseEntity.ok(shareAndHelpApiMapper.toResponse(result));
    }

    @PatchMapping("/{postId}/reopen")
    public ResponseEntity<ShareAndHelpPostResponse> reopenPost(
                    @PathVariable("postId") final UUID postId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();
        final UUID buildingId = resolveBuildingIdForCurrentUser(currentUser);

        final ShareAndHelpPostResult result =
                        updateShareAndHelpPostStatusUseCase.updatePostStatus(
                                        new UpdateShareAndHelpPostStatusCommand(postId,
                                                                                buildingId,
                                                                                currentUser.userId(),
                                                                                ShareAndHelpPostStatus.OPEN));

        return ResponseEntity.ok(shareAndHelpApiMapper.toResponse(result));
    }

    private UUID resolveBuildingIdForCurrentUser(final CurrentUser currentUser) {

        final UserRole role = UserRole.from(currentUser.role());

        if (role.usesManagedBuilding()) {
            return loadManagerBuildingPort.loadManagedBuildingIdByManagerUserId(currentUser.userId());
        }

        return loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(currentUser.userId());
    }
}