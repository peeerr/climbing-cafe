package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.peeerr.climbing.exception.ErrorMessage.*;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberEditRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{2,16}$", message = INVALID_USERNAME_LENGTH)
    @NotBlank(message = USERNAME_NOT_BLANK)
    private String username;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = INVALID_EMAIL_PATTERN)
    @NotBlank(message = EMAIL_NOT_BLANK)
    private String email;

    public static MemberEditRequest of(String username, String email) {
        return new MemberEditRequest(username, email);
    }

}
