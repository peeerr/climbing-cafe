package com.peeerr.climbing.service;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.exception.AccessDeniedException;
import com.peeerr.climbing.exception.ValidationException;
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
        validateDuplicateUser(request.getUsername(), request.getEmail());
        validatePassword(request.getPassword(), request.getCheckPassword());

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

        validateUserAccess(member, loginId);

        if (!member.getUsername().equals(request.getUsername())) {
            validateDuplicateUsername(request.getUsername());
            member.changeUsername(request.getUsername());
        }

        if (!member.getEmail().equals(request.getEmail())) {
            validateDuplicateEmail(request.getEmail());
            member.changeEmail(request.getEmail());
        }
    }

    private void validateDuplicateUser(String username, String email) {
        validateDuplicateUsername(username);
        validateDuplicateEmail(email);
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

    private void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new ValidationException(ErrorMessage.PASSWORD_CONFIRMATION_FAILED);
        }
    }

    private void validateUserAccess(Member member, Long loginId) {
        if (!member.getId().equals(loginId)) {
            throw new AccessDeniedException();
        }
    }

}
