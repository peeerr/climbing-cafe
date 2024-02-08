package com.peeerr.climbing.controller;

import com.peeerr.climbing.annotation.OwnerCheck;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.member.request.MemberCreateRequest;
import com.peeerr.climbing.dto.member.request.MemberEditRequest;
import com.peeerr.climbing.dto.member.response.MemberResponse;
import com.peeerr.climbing.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.peeerr.climbing.annotation.OwnerCheck.MemberIdSource;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse> memberAdd(@RequestBody @Valid MemberCreateRequest memberCreateRequest,
                                                 BindingResult bindingResult) {
        Long memberId = memberService.addMember(memberCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(memberId));
    }

    @OwnerCheck(source = MemberIdSource.ARGUMENT)
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse> memberEdit(@PathVariable Long memberId,
                                                  @RequestBody @Valid MemberEditRequest memberEditRequest,
                                                  BindingResult bindingResult) {
        MemberResponse memberResponse = memberService.editMember(memberId, memberEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success(memberResponse));
    }

}
