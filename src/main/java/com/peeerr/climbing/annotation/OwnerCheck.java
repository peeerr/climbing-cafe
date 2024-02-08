package com.peeerr.climbing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OwnerCheck {

    enum MemberIdSource {
        ARGUMENT,
        SERVICE
    }

    MemberIdSource source() default MemberIdSource.SERVICE;

}
