package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.request.PostSearchCondition;
import com.peeerr.climbing.dto.post.response.PostDetailResponse;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    /*
     * 게시판, 검색어로 필터링된 모든 게시물 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse> postListFilteredByCategoryIdAndSearchWord(@RequestParam(required = false) final Long categoryId,
                                                                                 @ModelAttribute PostSearchCondition condition,
                                                                                 Pageable pageable) {
        Page<PostResponse> posts = postService.getPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.success(posts));
    }
    
    /*
     * 게시물 상세조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> postDetail(@PathVariable Long postId) {
        PostDetailResponse post = postService.getPostWithComments(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> postAdd(@RequestBody @Valid PostCreateRequest postCreateRequest,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.addPost(postCreateRequest, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> postEdit(@PathVariable Long postId,
                                                @RequestBody @Valid PostEditRequest postEditRequest,
                                                BindingResult bindingResult,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loginId = userDetails.getMember().getId();
        postService.editPost(postId, postEditRequest, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> postRemove(@PathVariable Long postId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loginId = userDetails.getMember().getId();
        postService.removePost(postId, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
