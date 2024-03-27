package com.peeerr.climbing.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberLoginRequest {

    @NotBlank(message = "이메일 아이디를 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    public static MemberLoginRequest of(String email, String password) {
        return new MemberLoginRequest(email, password);
    }

}
