package com.peeerr.climbing.validator;

import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.peeerr.climbing.exception.ErrorCode.ALREADY_EXISTS_USERNAME;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public void validateNewMember(MemberCreateRequest request) {
        validateDuplicateEmail(request.getEmail());
        validateDuplicateUsername(request.getUsername());
    }

    public void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ClimbingException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    public void validateDuplicateUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new ClimbingException(ALREADY_EXISTS_USERNAME);
        }
    }

}
