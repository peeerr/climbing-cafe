package com.peeerr.climbing.aop;

import com.peeerr.climbing.annotation.OwnerCheck;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.UnauthorizedAccessException;
import com.peeerr.climbing.service.PostService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class OwnerCheckAspect {

    private final PostService postService;

    @Before("@annotation(ownerCheck)")
    public void ownerCheck(JoinPoint jp, OwnerCheck ownerCheck) throws Throwable {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long ownerId = userDetails.getMember().getId();
        Long memberId = null;

        Object[] args = jp.getArgs();

        for (Object arg : args) {
            if (arg instanceof Long) {
                memberId = (Long) arg;
                break;
            }
        }

        if (memberId != null) {
            switch (ownerCheck.source()) {
                case SERVICE -> memberId = postService.getMember(memberId);
            }

            if (!memberId.equals(ownerId)) {
                throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
            }
        }
    }

}
