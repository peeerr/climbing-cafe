package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberCreateRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{2,16}$", message = "2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성해 주세요.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String username;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 영문자와 숫자를 최소 1개 이상 포함하여 입력해 주세요.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 다시 입력해 주세요.")
    private String checkPassword;

    @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="이메일 주소 양식을 확인해 주세요")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    public static MemberCreateRequest of(String username, String password, String checkPassword, String email) {
        return new MemberCreateRequest(username, password, checkPassword, email);
    }

}
