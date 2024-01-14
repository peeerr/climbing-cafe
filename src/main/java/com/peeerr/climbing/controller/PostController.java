package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.exception.ex.ValidationException;
import com.peeerr.climbing.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> postList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> posts = postService.getPosts(pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "게시물 전체 조회 성공", posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> postDetail(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "게시물 상세 조회 성공", response));
    }

    @PostMapping
    public ResponseEntity<?> postAdd(@RequestBody @Valid PostCreateRequest postCreateRequest,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException("유효성 검사 오류", errorMap);
        }

        postService.addPost(postCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("success", "게시물 작성 성공", null));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> postEdit(@PathVariable Long postId,
                                      @RequestBody @Valid PostEditRequest postEditRequest,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException("유효성 검사 오류", errorMap);
        }

        postService.editPost(postId, postEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "게시물 수정 성공", null));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> postRemove(@PathVariable Long postId) {
        postService.removePost(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "게시물 삭제 성공", null));
    }

}
