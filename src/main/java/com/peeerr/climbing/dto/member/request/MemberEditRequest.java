package com.peeerr.climbing.dto.member.request;

import jakarta.validation.constraints.*;
import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberEditRequest {

    @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,16}$", message = "2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성해 주세요.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String username;

    @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="이메일 주소 양식을 확인해 주세요")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    public static MemberEditRequest of(String username, String email) {
        return new MemberEditRequest(username, email);
    }

}
