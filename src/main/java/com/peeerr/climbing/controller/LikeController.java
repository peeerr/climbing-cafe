package com.peeerr.climbing.controller;

import com.peeerr.climbing.security.MemberPrincipal;
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

    @PostMapping
    public ResponseEntity<Void> likeAdd(@PathVariable Long postId,
                                        @AuthenticationPrincipal MemberPrincipal userDetails) {
        likeService.like(userDetails.getMember().getId(), postId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> likeRemove(@PathVariable Long postId,
                                           @AuthenticationPrincipal MemberPrincipal userDetails) {
        likeService.unlike(userDetails.getMember().getId(), postId);

        return ResponseEntity.ok()
                .build();
    }

}
