package com.why.buildingmanagement.shareandhelp.infrastructure.api.controller;

import com.why.buildingmanagement.shareandhelp.application.port.in.AdminDeleteCommentUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.AdminDeleteShareAndHelpPostUseCase;
import com.why.buildingmanagement.shareandhelp.application.port.in.AdminGetShareAndHelpPostsUseCase;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.ShareAndHelpPostResponse;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.mapper.ShareAndHelpApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/share-and-help/posts")
@RequiredArgsConstructor
public class AdminShareAndHelpController {

    private final AdminDeleteShareAndHelpPostUseCase adminDeleteShareAndHelpPostUseCase;
    private final AdminDeleteCommentUseCase adminDeleteCommentUseCase;

    private final AdminGetShareAndHelpPostsUseCase adminGetShareAndHelpPostsUseCase;
    private final ShareAndHelpApiMapper shareAndHelpApiMapper;

    @GetMapping
    public ResponseEntity<List<ShareAndHelpPostResponse>> getAllPostsForAdmin() {

        final List<ShareAndHelpPostResponse> response = adminGetShareAndHelpPostsUseCase.getAllPostsForAdmin()
                                                                                        .stream()
                                                                                        .map(shareAndHelpApiMapper::toResponse)
                                                                                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePostByAdmin(@PathVariable("postId") final UUID postId) {

        adminDeleteShareAndHelpPostUseCase.deletePostByAdmin(postId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentByAdmin(@PathVariable("postId") final UUID postId,
                                                     @PathVariable("commentId") final UUID commentId) {

        adminDeleteCommentUseCase.deleteCommentByAdmin(postId, commentId);

        return ResponseEntity.noContent().build();
    }
}