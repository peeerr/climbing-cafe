package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.peeerr.climbing.constant.ErrorMessage.EMAIL_NOT_BLANK;
import static com.peeerr.climbing.constant.ErrorMessage.PASSWORD_NOT_BLANK;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberLoginRequest {

    @NotBlank(message = EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = PASSWORD_NOT_BLANK)
    private String password;

    public static MemberLoginRequest of(String email, String password) {
        return new MemberLoginRequest(email, password);
    }

}
