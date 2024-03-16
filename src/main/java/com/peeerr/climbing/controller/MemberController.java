package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.member.request.MemberCreateRequest;
import com.peeerr.climbing.dto.member.request.MemberEditRequest;
import com.peeerr.climbing.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse> memberAdd(@RequestBody @Valid MemberCreateRequest memberCreateRequest,
                                                 BindingResult bindingResult) {
        memberService.addMember(memberCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse> memberEdit(@PathVariable Long memberId,
                                                  @RequestBody @Valid MemberEditRequest memberEditRequest,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loginId = userDetails.getMember().getId();
        memberService.editMember(memberId, memberEditRequest, loginId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
