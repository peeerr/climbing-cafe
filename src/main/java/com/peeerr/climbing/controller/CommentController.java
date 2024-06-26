package com.peeerr.climbing.controller;

import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.dto.comment.CommentCreateRequest;
import com.peeerr.climbing.dto.comment.CommentEditRequest;
import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse> commentAdd(@PathVariable Long postId,
                                                  @RequestBody @Valid CommentCreateRequest request,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal MemberPrincipal userDetails) {
        commentService.addComment(postId, request, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse> commentEdit(@PathVariable Long postId,
                                                   @PathVariable Long commentId,
                                                   @RequestBody @Valid CommentEditRequest request,
                                                   BindingResult bindingResult,
                                                   @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        commentService.editComment(commentId, request, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> commentRemove(@PathVariable Long postId,
                                                     @PathVariable Long commentId,
                                                     @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        commentService.removeComment(commentId, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
