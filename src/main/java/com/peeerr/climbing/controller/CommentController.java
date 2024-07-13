package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.request.CommentCreateRequest;
import com.peeerr.climbing.dto.request.CommentEditRequest;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> commentAdd(@PathVariable Long postId,
                                           @RequestBody @Valid CommentCreateRequest request,
                                           @AuthenticationPrincipal MemberPrincipal userDetails) {
        commentService.addComment(postId, request, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> commentEdit(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @RequestBody @Valid CommentEditRequest request,
                                            @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        commentService.editComment(commentId, request, loginId);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> commentRemove(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        commentService.removeComment(commentId, loginId);

        return ResponseEntity.ok()
                .build();
    }

}
