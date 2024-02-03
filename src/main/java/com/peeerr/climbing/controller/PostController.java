package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<ApiResponse> postList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> posts = postService.getPosts(pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.success(posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> postDetail(@PathVariable Long postId) {
        PostResponse post = postService.getPost(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> postAdd(@RequestBody @Valid PostCreateRequest postCreateRequest,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponse addedPost = postService.addPost(postCreateRequest, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(addedPost));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> postEdit(@PathVariable Long postId,
                                      @RequestBody @Valid PostEditRequest postEditRequest,
                                      BindingResult bindingResult) {
        PostResponse editedPost = postService.editPost(postId, postEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success(editedPost));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> postRemove(@PathVariable Long postId) {
        postService.removePost(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
