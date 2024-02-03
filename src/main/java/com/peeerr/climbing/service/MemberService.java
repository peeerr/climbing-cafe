package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.user.request.UserCreateRequest;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long registerUser(UserCreateRequest userCreateRequest) {
        if (!userCreateRequest.getPassword().equals(userCreateRequest.getCheckPassword())) {
            throw new ValidationException(ErrorMessage.PASSWORD_CONFIRMATION_FAILED);
        }

        Member member = Member.builder()
                .username(userCreateRequest.getUsername())
                .email(userCreateRequest.getEmail())
                .password(passwordEncoder.encode(userCreateRequest.getPassword()))
                .build();

        Member savedEntity = memberRepository.save(member);

        return savedEntity.getId();
    }

}
