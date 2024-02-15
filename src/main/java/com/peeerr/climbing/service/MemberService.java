package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.member.request.MemberCreateRequest;
import com.peeerr.climbing.dto.member.request.MemberEditRequest;
import com.peeerr.climbing.dto.member.response.MemberResponse;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.DuplicationException;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.UnauthorizedAccessException;
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

    @Transactional
    public MemberResponse editMember(Long memberId, MemberEditRequest request, Long loginId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND));

        if (!member.getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        if (!member.getUsername().equals(request.getUsername())) {
            validateDuplicateUsername(request.getUsername());
            member.changeUsername(request.getUsername());
        }

        if (!member.getEmail().equals(request.getEmail())) {
            validateDuplicateEmail(request.getEmail());
            member.changeEmail(request.getEmail());
        }

        MemberResponse response = MemberResponse.from(member);

        return response;
    }

    public void validateDuplicateUser(String username, String email) {
        Optional<Member> existingUsername = memberRepository.findMemberByUsername(username);
        Optional<Member> existingEmail = memberRepository.findMemberByEmail(email);

        existingUsername.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.USERNAME_DUPLICATED);
        });
        existingEmail.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.EMAIL_DUPLICATED);
        });
    }

    public void validateDuplicateUsername(String username) {
        Optional<Member> existingUsername = memberRepository.findMemberByUsername(username);

        existingUsername.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.USERNAME_DUPLICATED);
        });
    }

    public void validateDuplicateEmail(String email) {
        Optional<Member> existingEmail = memberRepository.findMemberByEmail(email);

        existingEmail.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.EMAIL_DUPLICATED);
        });
    }

}
