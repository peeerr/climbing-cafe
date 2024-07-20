package com.peeerr.climbing.dto.request.validation;

import com.peeerr.climbing.dto.request.MemberCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String message;
    private String field;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        return obj instanceof MemberCreateRequest request && isValidRequest(request, context);
    }

    private boolean isValidRequest(MemberCreateRequest request, ConstraintValidatorContext context) {
        boolean isValid = request.getPassword().equals(request.getCheckPassword());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.field)
                    .addConstraintViolation();
        }
        return isValid;
    }

}
