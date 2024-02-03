package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.user.request.UserCreateRequest;
import com.peeerr.climbing.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse> signupUser(@RequestBody @Valid UserCreateRequest userCreateRequest,
                                              BindingResult bindingResult) {
        Long userId = memberService.registerUser(userCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }

}
