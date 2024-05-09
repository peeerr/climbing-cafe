package com.peeerr.climbing.controller;

import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/likes")
@RestController
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> likeCount(@PathVariable Long postId) {
        Long likeCount = likeService.getLikeCount(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(likeCount));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> likeAdd(@PathVariable Long postId,
                                               @AuthenticationPrincipal MemberPrincipal userDetails) {
        likeService.like(userDetails.getMember().getId(), postId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> likeRemove(@PathVariable Long postId,
                                                  @AuthenticationPrincipal MemberPrincipal userDetails) {
        likeService.unlike(userDetails.getMember().getId(), postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
