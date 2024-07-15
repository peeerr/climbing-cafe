package com.peeerr.climbing.validation.validator;

import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.validation.annotation.NotDuplicateEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotDuplicateEmailValidator implements ConstraintValidator<NotDuplicateEmail, String> {

    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value == null) ? true : !memberRepository.existsByEmail(value);
    }

}
