package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.exception.AccessDeniedException;
import com.peeerr.climbing.exception.already.AlreadyExistsEmailException;
import com.peeerr.climbing.exception.already.AlreadyExistsUsernameException;
import com.peeerr.climbing.exception.notfound.MemberNotFoundException;
import com.peeerr.climbing.repository.MemberRepository;
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
    public void addMember(MemberCreateRequest request) {
        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void editMember(Long memberId, MemberEditRequest request, Long loginId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        checkOwner(loginId, memberId);

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
                    throw new AlreadyExistsUsernameException();
                });
    }

    private void validateDuplicateEmail(String email) {
        memberRepository.findMemberByEmail(email)
                .ifPresent(foundMember -> {
                    throw new AlreadyExistsEmailException();
                });
    }

    private void checkOwner(Long loginId, Long ownerId) {
        if (!loginId.equals(ownerId)) {
            throw new AccessDeniedException();
        }
    }

}
