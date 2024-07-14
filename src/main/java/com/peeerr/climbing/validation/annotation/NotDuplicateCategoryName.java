package com.peeerr.climbing.validation.annotation;

import com.peeerr.climbing.validation.validator.NotDuplicateCategoryNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotDuplicateCategoryNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotDuplicateCategoryName {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
