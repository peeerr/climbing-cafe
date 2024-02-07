package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.user.request.MemberCreateRequest;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.DuplicationException;
import com.peeerr.climbing.exception.ex.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long addMember(MemberCreateRequest request) {
        validateDuplicateUser(request.getUsername(), request.getEmail());

        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new ValidationException(ErrorMessage.PASSWORD_CONFIRMATION_FAILED);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Member savedEntity = memberRepository.save(member);

        return savedEntity.getId();
    }

    public void validateDuplicateUser(String username, String email) {
        Optional<Member> existingMember = memberRepository.findUserByUsernameOrEmail(username, email);

        existingMember.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.MEMBER_DUPLICATED);
        });
    }


}
