package com.peeerr.climbing.dto.request;

import com.peeerr.climbing.validation.annotation.NotDuplicateEmail;
import com.peeerr.climbing.validation.annotation.NotDuplicateUsername;
import com.peeerr.climbing.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.peeerr.climbing.exception.ErrorMessage.*;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@PasswordMatches(message = PASSWORD_MISMATCH, field = "checkPassword")
public class MemberCreateRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{2,16}$", message = INVALID_USERNAME_LENGTH)
    @NotBlank(message = USERNAME_NOT_BLANK)
    @NotDuplicateUsername(message = USERNAME_NOT_DUPLICATE)
    private String username;

    @Size(min = 8, max = 20, message = INVALID_PASSWORD_LENGTH)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = INVALID_PASSWORD_PATTERN)
    @NotBlank(message = PASSWORD_NOT_BLANK)
    private String password;

    @NotBlank(message = CHECK_PASSWORD_NOT_BLANK)
    private String checkPassword;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = INVALID_EMAIL_PATTERN)
    @NotBlank(message = EMAIL_NOT_BLANK)
    @NotDuplicateEmail(message = EMAIL_NOT_DUPLICATE)
    private String email;

    public static MemberCreateRequest of(String username, String password, String checkPassword, String email) {
        return new MemberCreateRequest(username, password, checkPassword, email);
    }

}
