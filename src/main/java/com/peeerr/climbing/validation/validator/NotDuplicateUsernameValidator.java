package com.peeerr.climbing.validation.validator;

import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.validation.annotation.NotDuplicateUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotDuplicateUsernameValidator implements ConstraintValidator<NotDuplicateUsername, String> {

    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !memberRepository.existsByUsername(value);
    }

}
