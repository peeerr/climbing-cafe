package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.request.PostCreateRequest;
import com.peeerr.climbing.dto.request.PostEditRequest;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PostDetailResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> postListFilteredByCategoryIdAndSearchWord(@RequestParam(required = false) final Long categoryId,
                                                                                 @ModelAttribute PostSearchCondition condition,
                                                                                 Pageable pageable) {
        Page<PostResponse> posts = postService.getPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.of(posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> postDetail(@PathVariable Long postId) {
        PostDetailResponse post = postService.getPostWithComments(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.of(post));
    }

    @PostMapping
    public ResponseEntity<Void> postAdd(@RequestBody @Valid PostCreateRequest postCreateRequest,
                                        @AuthenticationPrincipal MemberPrincipal userDetails) {
        postService.addPost(postCreateRequest, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> postEdit(@PathVariable Long postId,
                                         @RequestBody @Valid PostEditRequest postEditRequest,
                                         @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        postService.editPost(postId, postEditRequest, loginId);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> postRemove(@PathVariable Long postId,
                                           @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        postService.removePost(postId, loginId);

        return ResponseEntity.ok()
                .build();
    }

}
