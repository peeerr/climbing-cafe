package com.peeerr.climbing.service;

import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void addMember(MemberCreateRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ClimbingException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new ClimbingException(ErrorCode.ALREADY_EXISTS_USERNAME);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    public void editMember(Long memberId, MemberEditRequest request, Long loginId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.MEMBER_NOT_FOUND));
        member.checkOwner(loginId);

        updateUsernameIfNecessary(member, request.getUsername());
        updateEmailIfNecessary(member, request.getEmail());
    }

    private void updateUsernameIfNecessary(Member member, String newUsername) {
        if (!member.getUsername().equals(newUsername)) {
            validateDuplicateUsername(newUsername);
            member.changeUsername(newUsername);
        }
    }

    private void updateEmailIfNecessary(Member member, String newEmail) {
        if (!member.getEmail().equals(newEmail)) {
            validateDuplicateEmail(newEmail);
            member.changeEmail(newEmail);
        }
    }

    private void validateDuplicateUsername(String username) {
        memberRepository.findMemberByUsername(username)
                .ifPresent(foundMember -> {
                    throw new ClimbingException(ErrorCode.ALREADY_EXISTS_USERNAME);
                });
    }

    private void validateDuplicateEmail(String email) {
        memberRepository.findMemberByEmail(email)
                .ifPresent(foundMember -> {
                    throw new ClimbingException(ErrorCode.ALREADY_EXISTS_EMAIL);
                });
    }

}
