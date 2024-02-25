package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.request.PostSearchCondition;
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

    // TODO: 쿼리 줄이기 + 요청 지금 느림
    @GetMapping
    public ResponseEntity<ApiResponse> postListFilteredByBoardIdAndSearchWord(@RequestParam(required = false) final Long boardId,
                                                          @ModelAttribute PostSearchCondition condition,
                                                          @PageableDefault(size = 20, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> posts = postService.getPostsFilteredByBoardIdAndSearchWord(boardId, condition, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.success(posts));
    }

//    // TODO: 위에 API 랑 URL도 같고 요청 파라미터도 같아서 충돌 문제 해결
//    // TODO: 이거 하나 실행하는데 쿼리가 4개(게시물 + (멤버 + 카테고리 + 댓글)) 날라감
//    @GetMapping("/{postId}")
//    public ResponseEntity<ApiResponse> postDetail(@PathVariable Long postId) {
//        PostWithCommentsResponse post = postService.getPostWithComments(postId);
//
//        return ResponseEntity.ok()
//                .body(ApiResponse.success(post));
//    }


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
                                                BindingResult bindingResult,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loginId = userDetails.getMember().getId();
        PostResponse editedPost = postService.editPost(postId, postEditRequest, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(editedPost));
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
